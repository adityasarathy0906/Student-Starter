pipeline {
    agent any

    tools {
        jdk 'JDK17'
        maven 'Maven'
    }

    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Clean and Test') {
            steps {
                sh 'mvn clean test'
            }
            post {
                always {
                    junit 'course-registration-app/target/surefire-reports/TEST-*.xml'
                }
            }
        }

        stage('Package') {
            steps {
                sh 'mvn package'
            }
        }

        stage('Archive Artifact') {
            steps {
                archiveArtifacts artifacts: 'course-registration-app/target/*.jar',
                                 fingerprint: true
            }
        }
    }
}

