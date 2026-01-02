#!/usr/bin/env groovy

import org.jenkinsPipeline.Constants


/**
 * Ensures base Kubernetes infrastructure exists (namespace and service).
 * This is common to ALL deployment strategies (Blue/Green, Canary, Rolling).
 * 
 * @param serviceName The name of the service (e.g., 'lms-authnusers-command')
 * @param namespace The Kubernetes namespace (e.g., 'dev', 'staging', 'prod')
 * 
 */
def call(String serviceName, String namespace) 
{
    echo """==========================================
            Kubernetes Infrastructure Setup
        ==========================================
            Service: ${serviceName}
            Namespace: ${namespace}
        =========================================="""
    
    withCredentials([file(credentialsId: Constants.ENVIRONMENT_2_CREDENTIALS_ID[namespace], variable: 'KUBECONFIG')]) 
    {
        // 1. Verifica/Cria Namespace
        def namespaceExists = false
        try 
        {
            if (isUnix()) 
            {
                def result = sh(
                    script: "kubectl get namespace ${namespace} --no-headers 2>&1",
                    returnStatus: true
                )
                namespaceExists = (result == 0)
            } 
            else 
            {
                def result = bat(
                    script: "@kubectl get namespace ${namespace} --no-headers 2>nul",
                    returnStatus: true
                )
                namespaceExists = (result == 0)
            }
        } 
        catch (Exception e) 
        {
            namespaceExists = false
        }
        
        if (!namespaceExists) 
        {
            echo "Namespace ${namespace} does not exist. Creating it..."
            
            if (isUnix()) 
            {
                sh "kubectl apply -f ../infra/${namespace}-namespace.yaml"
            } 
            else 
            {
                bat "kubectl apply -f ..\\infra\\${namespace}-namespace.yaml"
            }
            
            echo "Namespace ${namespace} created successfully"
        } 
        else 
        {
            echo "Namespace ${namespace} already exists"
        }

        // 2. Verifica/Cria Service
        def serviceExists = false
        try 
        {
            if (isUnix()) 
            {
                sh "kubectl get service ${serviceName}-service -n ${namespace}"
                serviceExists = true
            } 
            else 
            {
                bat "@kubectl get service ${serviceName}-service -n ${namespace} >nul 2>&1"
                serviceExists = true
            }
        } 
        catch (Exception e) 
        {
            serviceExists = false
        }
        
        if (!serviceExists) 
        {
            echo "Creating Service ${serviceName}-service..."
            
            def serviceFile = "infra/${serviceName}-service.yaml"
            
            if (isUnix()) 
            {
                sh "kubectl apply -f ${serviceFile} -n ${namespace}"
            } 
            else 
            {
                bat "kubectl apply -f ${serviceFile}"
            }
            
            echo "Service created successfully"
        } 
        else 
        {
            echo "Service ${serviceName}-service already exists"
        }
        
        // 3. Garante que RabbitMQ está a correr
        echo "Ensuring RabbitMQ is running..."
        EnsureRabbitMQRunning(namespace)
        
        echo """==========================================
                Kubernetes Infrastructure Ready
            ==========================================
                Namespace: ${namespace}
                Service: ${serviceName}-service
                RabbitMQ: Running
            =========================================="""
    }
}
