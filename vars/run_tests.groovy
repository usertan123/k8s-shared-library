// vars/run_tests.groovy
def call(Map config = [:]) {
    // Default test command if not provided
    def testCommand = config.testCommand ?: "npm test"
    
    def nodeTool = config.nodeTool ?: "NodeJS 18.0.0"

    echo "Running unit tests with Node tool: ${nodeTool}"

    echo "Running unit tests with command: ${testCommand}"

    // Execute the test command
    // sh """
    //     ${testCommand}
    // """
    withEnv(["PATH+NODE=${tool nodeTool}/bin"]) {
        sh """
            npm install
            ${testCommand}
        """
    }
}
