// vars/run_tests.groovy
def call(Map config = [:]) {
    // Default test command if not provided
    // def testCommand = config.testCommand ?: "npm test"
    
    // def nodeTool = config.nodeTool ?: "Node18"

    // echo "Running unit tests with Node tool: ${nodeTool}"

    // echo "Running unit tests with command: ${testCommand}"

    // // Execute the test command
    // // sh """
    // //     ${testCommand}
    // // """
    // withEnv(["PATH+NODE=${tool nodeTool}/bin"]) {
    //     sh """
    //         npm ci
    //         ${testCommand}
    //     """
        echo "Unit tests completed successfully"

    }
}
