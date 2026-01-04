#!/usr/bin/env groovy

import org.jenkinsPipeline.Constants

/**
 * Ensures stable deployment exists for Canary strategy
 *
 * This function:
 * 1. Checks if stable deployment exists
 * 2. If not, creates it with initial image
 * 3. Creates/verifies service selector points to stable
 * 4. Waits for deployment to be ready
 *
 * This is ONLY needed for Canary strategy (Blue/Green uses its own color deployments)
 *
 * @param serviceName The name of the service (e.g., 'lendings-q')
 * @param dockerImage The Docker image to deploy (e.g., 'registry/image:version')
 * @param namespace The Kubernetes namespace (e.g., 'dev')
 */
def call(String serviceName, String dockerImage, String namespace)
{
    echo """==========================================
            ENSURING STABLE DEPLOYMENT
        ==========================================
            Service: ${serviceName}
            Image: ${dockerImage}
            Namespace: ${namespace}
        =========================================="""

    withCredentials([file(credentialsId: Constants.ENVIRONMENT_2_CREDENTIALS_ID[namespace], variable: 'KUBECONFIG')])
            {
                try
                {
                    // STEP 1: Check if stable deployment already exists
                    echo "STEP 1: Checking if stable deployment exists..."

                    def deploymentExists = false
                    try
                    {
                        if (isUnix())
                        {
                            sh(
                                    script: "kubectl get deployment ${serviceName} -n ${namespace} 2>/dev/null",
                                    returnStatus: true
                            )
                            deploymentExists = true
                        }
                        else
                        {
                            bat(
                                    script: "@kubectl get deployment ${serviceName} -n ${namespace} >nul 2>&1",
                                    returnStatus: true
                            )
                            deploymentExists = true
                        }
                    }
                    catch (Exception e)
                    {
                        deploymentExists = false
                    }

                    if (deploymentExists)
                    {
                        echo "✅ Stable deployment ${serviceName} already exists"

                        if (isUnix())
                        {
                            sh "kubectl get deployment ${serviceName} -n ${namespace} -o wide"
                        }
                        else
                        {
                            bat "kubectl get deployment ${serviceName} -n ${namespace} -o wide"
                        }
                    }
                    else
                    {
                        // STEP 2: Create stable deployment
                        echo "STEP 2: Creating stable deployment ${serviceName}..."

                        // Create YAML content inline (sem depender de ficheiros na pasta infra)
                        def deploymentYaml = """apiVersion: apps/v1
kind: Deployment
metadata:
  name: ${serviceName}
  namespace: ${namespace}
  labels:
    app: ${serviceName}
    version: stable
spec:
  replicas: 2
  selector:
    matchLabels:
      app: ${serviceName}
      version: stable
  template:
    metadata:
      labels:
        app: ${serviceName}
        version: stable
    spec:
      containers:
      - name: ${serviceName}
        image: ${dockerImage}
        imagePullPolicy: IfNotPresent
        ports:
        - name: http
          containerPort: 8882
        env:
        - name: RABBITMQ_HOST
          value: rabbitmq
        - name: RABBITMQ_PORT
          value: "5672"
        resources:
          requests:
            memory: "256Mi"
            cpu: "100m"
          limits:
            memory: "512Mi"
            cpu: "500m"
        livenessProbe:
          httpGet:
            path: /actuator/health/liveness
            port: http
          initialDelaySeconds: 30
          periodSeconds: 10
        readinessProbe:
          httpGet:
            path: /actuator/health/readiness
            port: http
          initialDelaySeconds: 10
          periodSeconds: 5
"""

                        if (isUnix())
                        {
                            writeFile file: "stable-deployment.yaml", text: deploymentYaml
                            sh "kubectl apply -f stable-deployment.yaml -n ${namespace}"
                        }
                        else
                        {
                            writeFile file: "stable-deployment.yaml", text: deploymentYaml
                            bat "kubectl apply -f stable-deployment.yaml -n ${namespace}"
                        }

                        echo "✅ Stable deployment ${serviceName} created"
                    }

                    // STEP 3: Ensure service exists and points to stable
                    echo "STEP 3: Ensuring service points to stable deployment..."

                    def serviceExists = false
                    try
                    {
                        if (isUnix())
                        {
                            sh(
                                    script: "kubectl get service ${serviceName}-service -n ${namespace} 2>/dev/null",
                                    returnStatus: true
                            )
                            serviceExists = true
                        }
                        else
                        {
                            bat(
                                    script: "@kubectl get service ${serviceName}-service -n ${namespace} >nul 2>&1",
                                    returnStatus: true
                            )
                            serviceExists = true
                        }
                    }
                    catch (Exception e)
                    {
                        serviceExists = false
                    }

                    if (!serviceExists)
                    {
                        echo "Creating service ${serviceName}-service..."

                        def serviceYaml = """apiVersion: v1
kind: Service
metadata:
  name: ${serviceName}-service
  namespace: ${namespace}
  labels:
    app: ${serviceName}
spec:
  type: ClusterIP
  ports:
  - port: 80
    targetPort: 8882
    protocol: TCP
    name: http
  selector:
    app: ${serviceName}
    version: stable
  sessionAffinity: None
"""

                        if (isUnix())
                        {
                            writeFile file: "stable-service.yaml", text: serviceYaml
                            sh "kubectl apply -f stable-service.yaml -n ${namespace}"
                        }
                        else
                        {
                            writeFile file: "stable-service.yaml", text: serviceYaml
                            bat "kubectl apply -f stable-service.yaml -n ${namespace}"
                        }

                        echo "✅ Service ${serviceName}-service created"
                    }
                    else
                    {
                        echo "✅ Service ${serviceName}-service already exists"

                        // Verify service selector points to stable
                        def selector
                        if (isUnix())
                        {
                            selector = sh(
                                    script: "kubectl get service ${serviceName}-service -n ${namespace} -o jsonpath='{.spec.selector.version}'",
                                    returnStdout: true
                            ).trim()
                        }
                        else
                        {
                            selector = bat(
                                    script: "@kubectl get service ${serviceName}-service -n ${namespace} -o jsonpath=\"{.spec.selector.version}\"",
                                    returnStdout: true
                            ).trim()
                        }

                        if (selector == "stable")
                        {
                            echo "✅ Service selector correctly points to stable"
                        }
                        else
                        {
                            echo "⚠️  Service selector points to: ${selector}"
                            echo "Updating service to point to stable..."

                            if (isUnix())
                            {
                                sh """
                            kubectl patch service ${serviceName}-service -n ${namespace} \
                                -p '{"spec":{"selector":{"app":"${serviceName}","version":"stable"}}}'
                        """
                            }
                            else
                            {
                                bat """
                            kubectl patch service ${serviceName}-service -n ${namespace} ^
                                -p "{\"spec\":{\"selector\":{\"app\":\"${serviceName}\",\"version\":\"stable\"}}}"
                        """
                            }

                            echo "✅ Service selector updated to stable"
                        }
                    }

                    // STEP 4: Wait for deployment to be ready
                    echo "STEP 4: Waiting for stable deployment to be ready..."

                    if (isUnix())
                    {
                        sh """
                    kubectl wait --for=condition=available --timeout=300s \
                        deployment/${serviceName} -n ${namespace} || true
                """
                    }
                    else
                    {
                        bat """
                    kubectl wait --for=condition=available --timeout=300s ^
                        deployment/${serviceName} -n ${namespace} || exit /b 0
                """
                    }

                    // STEP 5: Verify deployment status
                    echo "STEP 5: Verifying deployment status..."

                    if (isUnix())
                    {
                        sh "kubectl get deployment ${serviceName} -n ${namespace} -o wide"
                    }
                    else
                    {
                        bat "kubectl get deployment ${serviceName} -n ${namespace} -o wide"
                    }

                    echo """==========================================
                    STABLE DEPLOYMENT READY
                ==========================================
                    Service: ${serviceName}
                    Deployment: ${serviceName}
                    Replicas: 2 (stable)
                    Service Selector: stable
                =========================================="""
                }
                catch (Exception e)
                {
                    echo "❌ Error ensuring stable deployment: ${e.getMessage()}"
                    throw e
                }
            }
}