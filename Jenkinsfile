properties([
    pipelineTriggers([cron('*/2 * * * *')])
])

pipeline {
    agent any

    stages {
        stage('Build') {
            steps {
                sh "mvn build"               
                echo 'Building..'
            }
        }
        stage('Test') {
            steps {
                sh "mvn test"
                echo 'Testing..'
            }
        }
        stage('Deploy') {
            steps {
                echo 'Deploying....'
            }
        }
    }
}
