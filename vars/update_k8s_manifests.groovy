/**
 * Update Kubernetes manifests with new image tags
 */
def call(Map config = [:]) {
    def imageTag = config.imageTag ?: error("Image tag is required")
    def manifestsPath = config.manifestsPath ?: 'kubernetes'
    def gitCredentials = config.gitCredentials ?: 'github-credentials'
    def gitUserName = config.gitUserName ?: 'usertan123'
    def gitUserEmail = config.gitUserEmail ?: 'tan2018carlson@gmail.com'
    def gitBranch = config.gitBranch ?: 'main'  // default to main if not passed

    
    echo "Updating Kubernetes manifests with image tag: ${imageTag}"
    
    withCredentials([usernamePassword(
        credentialsId: gitCredentials,
        usernameVariable: 'GIT_USERNAME',
        passwordVariable: 'GIT_PASSWORD'
    )]) {
        // Configure Git
        sh """
            git config user.name "${gitUserName}"
            git config user.email "${gitUserEmail}"
        """
        
        // Update deployment manifests with new image tags - using proper Linux sed syntax
        sh """
            set -e
            # Update images
            sed -i "s|image: tanmaytech/easyshop-app:.*|image: tanmaytech/easyshop-app:${imageTag}|g" ${manifestsPath}/easyshop-deployment.yml

            if [ -f "${manifestsPath}/migration-job.yml" ]; then
                sed -i "s|image: tanmaytech/easyshop-migration:.*|image: tanmaytech/easyshop-migration:${imageTag}|g" ${manifestsPath}/migration-job.yml
            fi

            if [ -f "${manifestsPath}/nginx-ingress.yml" ]; then
                sed -i "s|host: .*|host: easyshop.techinferno.shop|g" ${manifestsPath}/nginx-ingress.yml
            fi

            if ! git diff --quiet; then
                echo "Changes detected. Committing and pushing..."
                git add ${manifestsPath}/*.yml
                git commit -m "Update image tags to ${imageTag} and ensure correct domain [ci skip]"
                git remote remove origin || true
                git remote add origin https://\$GIT_USERNAME:\$GIT_PASSWORD@github.com/usertan123/k8s-e-commerce-app.git
                git push origin HEAD:${gitBranch}
            else
                echo "No changes to commit"
            fi
        """

    }
}
// https://github.com/usertan123/k8s-e-commerce-app.git