pipeline {
  agent any
  parameters {
    gitParameter branchFilter: 'origin/(.*)', defaultValue: 'master', name: 'Branch', type: 'PT_BRANCH'
  }
stages {
    stage('Preparation'){
        steps {
        sh 'rm -rf *'
    } }
    stage('Cloning the Project') {
        steps {
            script {
            try {
                echo "flag:BRANCH ${params.Branch}"
                echo "flag:Server ${params.Server}"
                git branch: "${params.Branch}", credentialsId: 'nmr-github-token', url: 'https://github.com/devpms/nutriag.nmr.git'
            }       
            catch(Exception e) {
               echo "FAILED ${e}"
               currentBuild.result = 'FAILURE'
               throw e
            }
        }
}  }
stage('Removing the Old Code') {
        steps {
            script {
            try{        
             if( "${params.Server}" == 'Production')  {
                sh '''  
                cat <<EOF | ssh -o StrictHostKeyChecking=no ubuntu@ec2-44-199-23-164.compute-1.amazonaws.com 'cat - > /var/www/nutriag.nmr/before_update.sh'
                #!/bin/bash
                if [ -d "/var/www/nutriag.nmr" ]; then
                cd /var/www/nutriag.nmr
                rm -rf *
                echo "Removed"
                else
                echo "Failed to find the path to delete the contents inside the folder"
                exit 1
                fi
EOF
ssh ubuntu@ec2-44-199-23-164.compute-1.amazonaws.com bash /var/www/nutriag.nmr/before_update.sh
'''
            }
             else if("${params.Server}" == 'Development')  {
                sh '''  
                cat <<EOF | ssh -o StrictHostKeyChecking=no ubuntu@34.204.25.60 'cat - > /var/www/nutriag.nmr/before_update.sh'
                #!/bin/bash
                if [ -d "/var/www/nutriag.nmr" ]; then
                cd /var/www/nutriag.nmr
                rm -rf *
                echo "Removed"
                else
                echo "Failed to find the path to delete the contents inside the folder"
                exit 1
                fi
EOF
ssh ubuntu@34.204.25.60 bash /var/www/nutriag.nmr/before_update.sh
'''
             }
             }
             catch(Exception e) {
                echo "FAILED ${e}"
                currentBuild.result = 'FAILURE'
                throw e
        } }
}}
stage("Building the Code") {
        steps {
            script {
        try{
                 if( "${params.Server}" == 'Production')  {
                 sh 'tar -czf - ./ | ssh  ubuntu@ec2-44-199-23-164.compute-1.amazonaws.com "tar -C /var/www/nutriag.nmr/ -xzf -"'
           }
                 else if("${params.Server}" == 'Development')  {
                 sh 'tar -czf - ./ | ssh ubuntu@34.204.25.60 "tar -C /var/www/nutriag.nmr/ -xzf -"'  
          }  
    }
        catch(Exception e) {
             echo "FAILED ${e}"
             currentBuild.result = 'FAILURE'
             throw e
            } }
} }
        stage('Restarting Application') {
            steps {
                script {
            try{ 
                 if( "${params.Server}" == 'Production') {
                     sh '''
                    cat <<EOF | ssh ubuntu@ec2-44-199-23-164.compute-1.amazonaws.com 'cat - > /var/www/nutriag.nmr/restart.sh'
                    #!/bin/bash
                    echo $PATH
                    cd /var/www/nutriag.nmr
                    aws s3 cp s3://nutriage-env/nmr-live/.env . --profile nutriag_env_download_s3_access
                    ls
                    composer install
                    source /home/ubuntu/.nvm/nvm.sh
                    npm install
                    yes | php artisan migrate
                    yes | php artisan db:seed
                    php artisan optimize:clear
                    php artisan queue:restart
                    npm run prod
                    pm2 delete prod-nmr
                    pm2 start ecosystem.prod.config.js
                    
                    
EOF
ssh  ubuntu@ec2-44-199-23-164.compute-1.amazonaws.com bash /var/www/nutriag.nmr/restart.sh
                    '''
                 echo 'One way or another, I have finished'
                 }   
                 else if("${params.Server}" == 'Development') { 
                 sh '''
                 cat <<EOF | ssh ubuntu@34.204.25.60 'cat - > /var/www/nutriag.nmr/restart.sh'
                    #!/bin/bash
                    cd /var/www/nutriag.nmr
                    aws s3 cp s3://nutriage-env/nmr-dev/.env . --profile nutriag_env_download_s3_access
                    ls
                    composer install
                    source /home/ubuntu/.nvm/nvm.sh
                    npm install
                    yes | php artisan migrate
                    yes | php artisan db:seed
                    php artisan optimize:clear
                    php artisan queue:restart
                    npm run prod
                    pm2 delete dev-nmr
                    pm2 start ecosystem.dev.config.js
                
EOF
ssh ubuntu@34.204.25.60 bash /var/www/nutriag.nmr/restart.sh
                 '''
                 echo 'One way or another, I have finished'
                }
          }    
            catch(Exception e) {
                 echo "FAILED ${e}"
                 currentBuild.result = 'FAILURE'
                 throw e
             } }
} }
        
}
}