// Reusable declarative pipeline library file
// This can be loaded from a Jenkinsfile with: def call = load '.jenkins/pipeline.groovy'
// Or use a shared library in Jenkins for better reuse.

def call(Map params = [:]) {
    pipeline {
        agent any
        environment {
            MVN = params.get('mvn', 'mvn')
        }
        stages {
            stage('Checkout') {
                steps {
                    checkout scm
                }
            }
            stage('Build & Test') {
                steps {
                    sh "${MVN} -B -DskipTests=false clean test"
                }
                post {
                    always {
                        junit '**/target/surefire-reports/*.xml'
                    }
                }
            }
            stage('Package') {
                steps {
                    sh "${MVN} -B -DskipTests=true package"
                }
            }
            stage('Archive') {
                steps {
                    archiveArtifacts artifacts: 'target/*.jar, target/*.war, target/**/*.zip', fingerprint: true
                }
            }
        }
        post {
            always {
                echo "Pipeline finished"
            }
        }
    }
}
