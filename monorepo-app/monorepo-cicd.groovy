pipeline {
    agent any

    parameters {
        choice(name: 'Framework', choices: ['Next.js', 'React.js'], description: 'Select the framework to build and deploy')
        choice(name: 'Server', choices: ['Development', 'Production'], description: 'Select the server environment')
    }

    stages {
        stage('Cloning the Project') {
            steps {
                script {
                    try {
                        echo "Cloning the code"
                        git url: "https://gitlab.webelight.co.in/webelight/devops/training.git", branch: params.Framework == 'Next.js' ? "yatharth-new-pm2" : "yatharth-jenkins", credentialsId: "gitup"
                    } catch(Exception e) {
                        echo "FAILED ${e}"
                        currentBuild.result = 'FAILURE'
                        throw e
                    }
                }
            }
        }

        stage('Build and Deploy') {
            when {
                expression {
                    params.Framework == 'Next.js' || params.Framework == 'React.js'
                }
            }
            steps {
                script {
                    if (params.Framework == 'Next.js') {
                        echo "Building and deploying Next.js app"
                        sh '''
                            cd new-next-app
                            npm install
                            npm run build
                            npm install --prefix .next next
                            chmod -R 755 .next
                            npm install pm2 --no-save
                            npx pm2 start ecosystem.config.js
                        '''
                    } else if (params.Framework == 'React.js') {
                        echo "Building and deploying React.js app"
                        sh '''
                            rm -rf *
                            docker build -t my-new-app .
                            echo "Pushing the image to Docker Hub"
                            withCredentials([usernamePassword(credentialsId: "dockerhub", passwordVariable: "dockerHubPass", usernameVariable: "dockerHubUser")]) {
                                sh "docker tag my-new-app ${env.dockerHubUser}/my-new-app:latest"
                                sh "docker login -u ${env.dockerHubUser} -p ${env.dockerHubPass}"
                                sh "docker push ${env.dockerHubUser}/my-new-app:latest"
                            }
                            echo "Running the Docker container"
                            sh "docker run -d -p 3000:3000 yatharthchauhan2024/yc-app-jenkins:latest
                        '''
                    }
                }
            }
        }
    }
}
