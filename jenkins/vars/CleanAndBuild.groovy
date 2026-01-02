def call()
{
    echo 'Cleaning workspace...'
    
    // Verify we're in the correct directory
    if (isUnix()) 
    {
        sh "mvn clean compile test-compile"
    } 
    else 
    {
        bat "mvn clean compile test-compile"
    }
}