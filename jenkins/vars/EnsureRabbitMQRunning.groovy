#!/usr/bin/env groovy

import org.jenkinsPipeline.Constants

def call(String namespace, Integer timeoutMinutes = 5) 
{
    echo """==================================================
            Ensuring RabbitMQ is running in namespace: ${namespace}
            ==================================================="""

    withCredentials([file(credentialsId: Constants.ENVIRONMENT_2_CREDENTIALS_ID[namespace], variable: 'KUBECONFIG')]) 
    {
        try 
        {
            // Check if RabbitMQ deployment exists
            def deploymentExists = false
            try 
            {
                if (isUnix()) 
                {
                    sh "kubectl get deployment rabbitmq -n ${namespace}"
                } 
                else 
                {
                    bat "kubectl get deployment rabbitmq -n ${namespace}"
                }
                deploymentExists = true
                echo "RabbitMQ deployment already exists"
            } 
            catch (Exception e) 
            {
                echo "RabbitMQ deployment not found"
                deploymentExists = false
            }
            
            // Deploy RabbitMQ if it doesn't exist
            if (!deploymentExists) 
            {
                echo "Deploying RabbitMQ from manifest: ${Constants.RABBITMQ_MANIFEST_PATH}"
                
                try 
                {
                    if (isUnix()) 
                    {
                        sh "kubectl apply -f ${Constants.RABBITMQ_MANIFEST_PATH} -n ${namespace}"
                    } 
                    else 
                    {
                        bat "kubectl apply -f ${Constants.RABBITMQ_MANIFEST_PATH} -n ${namespace}"
                    }
                } 
                catch (Exception e) 
                {
                    // Ignore AlreadyExists errors for service - it's expected
                    if (e.message.contains("AlreadyExists")) 
                    {
                        echo "RabbitMQ service already exists (expected)"
                    } 
                    else 
                    {
                        throw e
                    }
                }
            }

            // Wait for RabbitMQ to be ready
            def startTime = System.currentTimeMillis()
            def timeoutMillis = timeoutMinutes * 60 * 1000
            def isReady = false
            
            while (!isReady && (System.currentTimeMillis() - startTime) < timeoutMillis) 
            {
                try 
                {
                    // Check if deployment has at least 1 ready replica
                    def readyReplicas
                    
                    if (isUnix()) 
                    {
                        readyReplicas = sh(
                            script: "kubectl get deployment rabbitmq -n ${namespace} -o jsonpath='{.status.readyReplicas}'",
                            returnStdout: true
                        ).trim()
                    }
                    else 
                    {
                        readyReplicas = bat(
                            script: "@echo off & kubectl get deployment rabbitmq -n ${namespace} -o jsonpath=\"{.status.readyReplicas}\"",
                            returnStdout: true
                        ).trim()
                    }
                    
                    if (readyReplicas && readyReplicas.toInteger() > 0) 
                    {
                        isReady = true
                        echo "RabbitMQ is ready!"
                    } 
                    else 
                    {
                        echo "Waiting for RabbitMQ to become ready... (${readyReplicas} ready replicas)"
                        sleep 5
                    }
                } 
                catch (Exception e) 
                {
                    echo "Error checking RabbitMQ status: ${e.message}"
                    sleep 5
                }
            }
            
            if (!isReady) 
            {
                error "RabbitMQ failed to become ready within ${timeoutMinutes} minutes"
            }
            
            // Verify RabbitMQ service is accessible
            echo "Verifying RabbitMQ service..."
            if (isUnix()) 
            {
                sh "kubectl get service rabbitmq -n ${namespace}"
            } 
            else 
            {
                bat "kubectl get service rabbitmq -n ${namespace}"
            }
            
            echo """==================================================
                    RabbitMQ is running and ready in ${namespace}
                    ===================================================
                """
            
            return true
            
        } 
        catch (Exception e) 
        {
            echo """==================================================
                    Failed to ensure RabbitMQ is running
                    Error: ${e.message}
                    ==================================================
                """
            throw e
        }
    }
}
