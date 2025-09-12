// vars/run_tests.groovy
def call(Map config = [:]) {
    // Default test command if not provided
    def testCommand = config.testCommand ?: "npm test"

    echo "Running unit tests with command: ${testCommand}"

    // Execute the test command
    sh """
        ${testCommand}
    """
}
