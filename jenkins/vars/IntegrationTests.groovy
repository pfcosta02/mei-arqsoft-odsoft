def call()
{
    echo 'Running integration tests...'
    if (isUnix()) 
    {
        // sh "mvn failsafe:integration-test failsafe:verify"
    } 
    else 
    {
        // bat "mvn failsafe:integration-test failsafe:verify"
    }
}