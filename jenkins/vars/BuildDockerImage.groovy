def call(String containerImageName)
{
    echo "Building Docker image: ${containerImageName}"
    if (isUnix()) 
    {
        sh "docker build -t ${containerImageName} ."
    } 
    else 
    {
        bat "docker build -t ${containerImageName} ."
    }
            
    echo "Docker image built successfully: ${containerImageName}"
}