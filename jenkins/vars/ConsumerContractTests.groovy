def call(boolean is_consumer)
{
    if (is_consumer)
    {
        echo 'Generate the contract json files and Publish them in a Pact broker...'
        if (isUnix()) 
        {
            sh "mvn -P publish-pacts clean verify"
        } 
        else 
        {
            bat "mvn -P publish-pacts clean verify"
        }

        // Archive Pact files
        echo 'Archiving Pact contract files...'
        archiveArtifacts artifacts: '**/target/pacts/*.json', allowEmptyArchive: true
        echo 'Run the consumer tests...'
        if (isUnix()) 
        {
            sh "mvn -P pact-consumer-tests test"
        } 
        else 
        {
            bat "mvn -P pact-consumer-tests test"
        }
    }
    else
    {
        echo 'Run the producer tests...'
        if (isUnix()) 
        {
            sh "mvn -P pact-provider-tests test"
        } 
        else 
        {
            bat "mvn -P pact-provider-tests test"
        }
    }
}