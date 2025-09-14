#!/usr/bin/env groovy

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
            # Update main application deployment - note the correct image name is tanmaytech/easyshop-app
            sed -i "s|image: tanmaytech/easyshop-app:.*|image: tanmaytech/easyshop-app:${imageTag}|g" ${manifestsPath}/easyshop-deployment.yml
            
            # Update migration job if it exists
            if [ -f "${manifestsPath}/migration-job.yml" ]; then
                sed -i "s|image: tanmaytech/easyshop-migration:.*|image: tanmaytech/easyshop-migration:${imageTag}|g" ${manifestsPath}/migration-job.yml
            fi
            
            # Ensure ingress is using the correct domain
            if [ -f "${manifestsPath}/nginx-ingress.yml" ]; then
                sed -i "s|host: .*|host: easyshop.techinferno.shop|g" ${manifestsPath}/nginx-ingress.yml
            fi
            
            # Check for changes
            if git diff --quiet; then
                echo "No changes to commit"
            else
                # Commit and push changes
                git add ${manifestsPath}/*.yml
                git commit -m "Update image tags to ${imageTag} and ensure correct domain [ci skip]"
                
                # Set up credentials for push
                # git remote set-url origin https://\${GIT_USERNAME}:\${GIT_PASSWORD}@github.com/usertan123/k8s-e-commerce-app.git
                git push https://${GIT_USERNAME}:${GIT_PASSWORD}@github.com/usertan123/k8s-e-commerce-app.git HEAD:${gitBranch}
            fi
        """
    }
}
// https://github.com/usertan123/k8s-e-commerce-app.git