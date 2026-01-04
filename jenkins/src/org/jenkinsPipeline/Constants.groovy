package org.jenkinsPipeline

class Constants 
{
    static final String DEV_NAMESPACE = 'dev' // Kubernetes namespace
    static final String STAGING_NAMESPACE = 'staging' // Kubernetes namespace
    static final String PROD_NAMESPACE = 'prod' // Kubernetes namespace


    static final String DOCKER_REGISTRY = "pfcosisep"
    static final String DOCKER_REGISTRY_D = "diogomanuel31"

    public static final LinkedHashMap<String, String> ENVIRONMENT_2_SONARQUBE_SERVER = [
                    'docker': 'sonarqube_docker',
                    'local' : 'sonarqube_local'
                ]

    public static final LinkedHashMap<String, String> ENVIRONMENT_2_CREDENTIALS_ID = [
                    'dev': 'kubeconfig-minikube-ci',
                    'staging' : 'kubeconfig-minikube-ci',
                    'prod' : 'kubeconfig-minikube-ci'
                ]

    static final String KUBECONFIG_CREDENTIALS_ID = 'kubeconfig-minikube-ci'
    static final String RABBITMQ_MANIFEST_PATH = '../infra/rabbitmq.yaml'
}