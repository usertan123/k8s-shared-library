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
            trivy fs ${path} -f json -o ${reportFile}
        """
    } else if (scanType == "image") {
        if (!imageName) {
            error "Image name is required for Trivy image scan"
        }
        sh """
            echo "Running Trivy Image scan..."
            trivy image --exit-code 1 --severity HIGH,CRITICAL ${imageName}:${imageTag} -f json -o ${reportFile}
        """
    }
    
    archiveArtifacts artifacts: reportFile, allowEmptyArchive: false
}
