def call()
{
    echo 'Running unit tests...'
    if (isUnix()) 
    {
        // sh "mvn surefire:test"
    }
    else 
    {
        // bat "mvn surefire:test"
    }
}