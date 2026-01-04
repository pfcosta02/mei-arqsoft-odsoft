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

    def stableDeployment = "${serviceName}-stable"         // lendings-q-stable
    def stableService    = "${serviceName}-service-stable" // lendings-q-service-stable

    echo """==========================================
            ENSURING STABLE DEPLOYMENT
        ==========================================
            Service: ${stableDeployment}
            Image: ${dockerImage}
            Namespace: ${namespace}
        =========================================="""

    withCredentials([file(credentialsId: Constants.ENVIRONMENT_2_CREDENTIALS_ID[namespace], variable: 'KUBECONFIG')])
            {
                try
                {
                    // STEP 1: Check if stable deployment already exists
                    echo "STEP 1: Checking if stable deployment exists..."

                    def deploymentExists
                    if (isUnix()) {
                        def status = sh(
                                script: "kubectl get deployment ${stableDeployment} -n ${namespace} >/dev/null 2>&1",
                                returnStatus: true
                        )
                        deploymentExists = (status == 0)
                    } else {
                        def status = bat(
                                script: "@kubectl get deployment ${stableDeployment} -n ${namespace} >nul 2>&1",
                                returnStatus: true
                        )
                        deploymentExists = (status == 0)
                    }

                    if (deploymentExists) {
                        echo "✅ Stable deployment ${stableDeployment} already exists"
                        if (isUnix()) {
                            sh "kubectl get deployment ${stableDeployment} -n ${namespace} -o wide"
                        } else {
                            bat "kubectl get deployment ${stableDeployment} -n ${namespace} -o wide"
                        }
                    } else {
                        // STEP 2: Create stable deployment
                        echo "STEP 2: Creating stable deployment ${stableDeployment}..."

                        // Create YAML content inline (sem depender de ficheiros na pasta infra)

                        def deploymentFile = "infra/${stableDeployment}.yaml"
                        def serviceFile    = "infra/${stableService}.yaml"


                        if (isUnix())
                        {
//                            writeFile file: "stable-deployment.yaml", text: deploymentYaml
                            sh "kubectl apply -f ${deploymentFile} -n ${namespace}"
                            sh "kubectl apply -f ${serviceFile}    -n ${namespace}"

                        }
                        else
                        {
//                            writeFile file: "stable-deployment.yaml", text: deploymentYaml

                            bat "kubectl apply -f ${deploymentFile} -n ${namespace}"
                            bat "kubectl apply -f ${serviceFile}    -n ${namespace}"

                        }

                        echo "✅ Stable deployment ${stableDeployment} created"
                    }

                    // STEP 3: Ensure service exists and points to stable
                    echo "STEP 3: Ensuring service points to stable deployment..."


                    def serviceExists
                    if (isUnix()) {
                        def status = sh(
                                script: "kubectl get service ${stableService} -n ${namespace} >/dev/null 2>&1",
                                returnStatus: true
                        )
                        serviceExists = (status == 0)
                    } else {
                        def status = bat(
                                script: "@kubectl get service ${stableService} -n ${namespace} >nul 2>&1",
                                returnStatus: true
                        )
                        serviceExists = (status == 0)
                    }

                    if (!serviceExists)
                    {
                        echo "Creating service ${stableService}..."

                        def serviceYaml = """apiVersion: v1
kind: Service
metadata:
  name: ${stableService}
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

                        echo "✅ Service ${stableService} created"
                    }
                    else
                    {
                        echo "✅ Service ${stableService} already exists"

                        // Verify service selector points to stable
                        def selector
                        if (isUnix())
                        {
                            selector = sh(
                                    script: "kubectl get service ${stableService} -n ${namespace} -o jsonpath='{.spec.selector.version}'",
                                    returnStdout: true
                            ).trim()
                        }
                        else
                        {
                            selector = bat(
                                    script: "@kubectl get service ${stableService} -n ${namespace} -o jsonpath=\"{.spec.selector.version}\"",
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
                            kubectl patch service ${stableService} -n ${namespace} \
                                -p '{"spec":{"selector":{"app":"${stableDeployment}","version":"stable"}}}'
                        """
                            }
                            else {
                                bat """
                                kubectl patch service ${stableService} -n ${namespace} ^
                                  --type=merge ^
                                  -p "{\\"spec\\":{\\"selector\\":{\\"app\\":\\"${stableDeployment}\\",\\"version\\":\\"stable\\"}}}"
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
                        deployment/${stableDeployment} -n ${namespace} || true
                """
                    }
                    else
                    {
                        bat """
                    kubectl wait --for=condition=available --timeout=300s ^
                        deployment/${stableDeployment} -n ${namespace} || exit /b 0
                """
                    }

                    // STEP 5: Verify deployment status
                    echo "STEP 5: Verifying deployment status..."

                    if (isUnix())
                    {
                        sh "kubectl get deployment ${stableDeployment} -n ${namespace} -o wide"
                    }
                    else
                    {
                        bat "kubectl get deployment ${stableDeployment} -n ${namespace} -o wide"
                    }

                    echo """==========================================
                    STABLE DEPLOYMENT READY
                ==========================================
                    Service: ${stableDeployment}
                    Deployment: ${stableDeployment}
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