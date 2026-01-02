def call()
{
    def version_to_return

    echo 'Building the final package...'
    if (isUnix()) 
    {
        sh "mvn package -DskipTests -DskipITs -DskipPitest"
    } 
    else 
    {
        bat "mvn package -DskipTests -DskipITs -DskipPitest"
    }
                
    // Extract project version
    if (isUnix()) 
    {
        version_to_return = sh(
            script: "mvn help:evaluate -Dexpression=\"project.version\" -q -DforceStdout",
            returnStdout: true
        ).trim()
    } 
    else 
    {
        version_to_return = bat(
            script: "@echo off && mvn help:evaluate -Dexpression=\"project.version\" -q -DforceStdout",
            returnStdout: true
        ).trim()
    }
    echo "Project version: ${version_to_return}"

    return version_to_return
}