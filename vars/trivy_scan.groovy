// vars/trivy_scan.groovy
def call(Map config = [:]) {
    def scanType   = config.scanType ?: "fs"   // "fs" or "image"
    def path       = config.path ?: "."        // used for fs scan
    def imageName  = config.imageName ?: ""
    def imageTag   = config.imageTag ?: "latest"
    
    // Build report file name dynamically
    def reportFile = config.reportFile ?: "trivy-${scanType}-${imageName.replaceAll('/', '_')}-${imageTag}.json"

    if (scanType == "fs") {
        sh """
            echo "Running Trivy FS scan..."
            trivy fs ${path} --severity HIGH,CRITICAL -f json -o ${reportFile}
        """
    } else if (scanType == "image") {
        if (!imageName) {
            error "Image name is required for Trivy image scan"
        }
            def cacheDir = "${env.WORKSPACE}/trivy-cache-${imageName.replaceAll('/', '_')}-${imageTag}"

        // sh """
        //     echo "Running Trivy Image scan..."
        //     trivy image --skip-db-update --exit-code 0 --severity HIGH,CRITICAL --skip-files .next/cache,.next/static --cache-dir ${cacheDir}   ${imageName}:${imageTag} -f json -o ${reportFile}
        // """
        sh """
            echo "Running Trivy Image scan..."
            trivy image --exit-code 0 --severity HIGH,CRITICAL --skip-files .next/cache,.next/static --cache-dir ${cacheDir}   ${imageName}:${imageTag} -f json -o ${reportFile}
        """
    }
    
    archiveArtifacts artifacts: reportFile, allowEmptyArchive: false
}
