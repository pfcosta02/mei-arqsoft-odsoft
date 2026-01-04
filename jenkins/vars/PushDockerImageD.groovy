import org.jenkinsPipeline.Constants

def call(String containerImage)
{
    echo 'Pushing Docker image to registry...'

    withCredentials([usernamePassword(
        credentialsId: 'dockerhub',
        usernameVariable: 'DOCKER_USER',
        passwordVariable: 'DOCKER_TOKEN'
    )]) 
    {
        // Push with version tag
        if (isUnix()) 
        {
            sh """
                    echo \$DOCKER_TOKEN | docker login -u \$DOCKER_USER --password-stdin
                    docker tag ${containerImage} ${Constants.DOCKER_REGISTRY_D}/${containerImage}
                    docker push ${Constants.DOCKER_REGISTRY_D}/${containerImage}
                """
        } 
        else 
        {
            bat """
                    docker logout || ver>NUL
                    echo %DOCKER_TOKEN% | docker login -u %DOCKER_USER% --password-stdin

                    docker tag ${containerImage} ${Constants.DOCKER_REGISTRY_D}/${containerImage}
                    docker push ${Constants.DOCKER_REGISTRY_D}/${containerImage}
                """
        }
    }
            
    echo "Docker image pushed: ${Constants.DOCKER_REGISTRY_D}/${containerImage}"
}