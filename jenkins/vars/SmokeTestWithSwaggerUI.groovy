def call(String serviceName, String namespace, int port) {

    def url = "http://localhost:${port}/swagger-ui/index.html"

    echo """
        ==========================================
        GENERIC SMOKE TEST
        ==========================================
        Service:   ${serviceName}
        Namespace: ${namespace}
        URL:       ${url}
        ==========================================
    """

    try {
        if (isUnix()) {
            sh """
                set -e
                echo "Calling ${url} ..."
                curl -f -s -o /dev/null "${url}"
            """
        } else {
            bat """
                echo Calling ${url} ...
                curl -f -s -o NUL "${url}"
                if %ERRORLEVEL% NEQ 0 (
                    echo Smoke test FAILED for ${url}
                    exit /b 1
                )
            """
        }

        echo "✅ Smoke test PASSED for ${url}"
    } catch (Exception e) {
        echo "❌ Smoke test FAILED for ${url}: ${e.getMessage()}"
        throw e
    }
}
