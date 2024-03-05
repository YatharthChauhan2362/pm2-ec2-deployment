pipeline {
    agent any

    parameters {
        choice(name: 'Server', choices: ['Development', 'Production'], description: 'Select the server environment')
    }

    stages {
        stage('Cloning the Project') {
            steps {
                script {
                    try {
                        echo "Cloning the code"
                        git url: "https://gitlab.webelight.co.in/webelight/devops/training.git", branch: "yatharth-new-pm2", credentialsId: "gitup"
                    } catch(Exception e) {
                        echo "FAILED ${e}"
                        currentBuild.result = 'FAILURE'
                        throw e
                    }
                }
            }
        }

        stage('Build and Deploy Development') {
            when {
                expression {
                    params.Server == 'Development'
                }
            }
            steps {
                script {
                    echo "Building and deploying on Development server"
                    sh '''
                        cd new-next-app
                        npm install
                        npm run build
                        npm install --prefix .next next
                        chmod -R 755 .next
                        npm install pm2 --no-save
                        npx pm2 start ecosystem.config.js
                    '''
                }
            }
        }

        stage('Build and Deploy Production') {
            when {
                expression {
                    params.Server == 'Production'
                }
            }
            steps {
                script {
                    echo "Building and deploying on Production server"
                    sh '''
                        cd new-next-app
                        npm install
                        npm run build
                        npm install --prefix .next next
                        chmod -R 755 .next
                        npm install pm2 --no-save
                        npx pm2 start ecosystem.config.js
                        # Additional production-specific steps can be added here
                    '''
                }
            }
        }
    }
}
