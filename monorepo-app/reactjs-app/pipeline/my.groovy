pipeline {
    agent any
    
    parameters {
        choice(choices: ['Production', 'Development'], description: 'Select the deployment environment', name: 'Server')
    }
    
    stages {
        stage('Preparation') {
            steps {
                sh 'rm -rf *'
            }
        }
        
        stage('Cloning the Project') {
            steps {
                script {
                    try {
                        echo "Selected Server: ${params.Server}"
                        git branch: 'master', url: 'https://github.com/devpms/nutriag.nmr.git'
                    } catch(Exception e) {
                        echo "FAILED ${e}"
                        currentBuild.result = 'FAILURE'
                        throw e
                    }
                }
            }
        }
        
        stage('Building the Code') {
            steps {
                script {
                    try {
                        // Your build commands here
                        sh 'echo "Build commands go here"'
                    } catch(Exception e) {
                        echo "FAILED ${e}"
                        currentBuild.result = 'FAILURE'
                        throw e
                    }
                }
            }
        }
        
        stage('Restarting Application') {
            steps {
                script {
                    try {
                        // Your application restart commands here
                        sh 'echo "Application restart commands go here"'
                    } catch(Exception e) {
                        echo "FAILED ${e}"
                        currentBuild.result = 'FAILURE'
                        throw e
                    }
                }
            }
        }
    }
}
