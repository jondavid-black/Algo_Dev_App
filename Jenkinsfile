#!/usr/bin/env groovy

// This pipeline depends on the scenario data pipelines having completed successfully.

pipeline {
    agent  any 

    stages {
        
        stage('Clean') {
            steps {
                sh 'gradle clean'
            }
        }

        stage('Build') {
            steps {
                sh 'gradle build'
            }
        }
        
        stage('Build Containers') {
            parallel {
                stage('Build Container for Scenario 1') {
                    steps {
                        sh 'gradle dockerBuildScenarioOne'
                        sh 'gradle dockerTagForPublishScenarioOne'
                        sh 'gradle dockerPushScenarioOne'
                    }
                }

                stage('Build Container for Scenario 2') {
                    steps {
                        sh 'gradle dockerBuildScenarioTwo'
                        sh 'gradle dockerTagForPublishScenarioTwo'
                        sh 'gradle dockerPushScenarioTwo'
                    }
                }
            }
        }

        stage('Prepare Kubernetes Cluster Storage') {
            steps {
                sh 'kubectl apply -f ./src/kube/output_volume.yml'
                sh 'kubectl apply -f ./src/kube/output_claim.yml'
            }
        }
        
        stage('Run Scenarios on Kubernetes Cluster') {
            parallel {
                stage('Run Scenario 1') {
                    steps {
                        sh 'kubectl apply -f ./src/kube/scenario1.yml'
                    }
                }

                stage('Run Scenario 2') {
                    steps {
                        sh 'kubectl apply -f ./src/kube/scenario2.yml'
                    }
                }
            }
        }

        stage('Wait for runs to complete') {
            steps {
                // TODO there's got to be a better way to do this
                sh 'while kubectl describe -f ./src/kube/scenario1.yml | grep -q "1 Running"; do sleep 2; done'
                sh 'while kubectl describe -f ./src/kube/scenario2.yml | grep -q "1 Running"; do sleep 2; done'
            }
        }

        stage('Clean Up Pods on Kubernetes Cluster') {
            steps {
                sh 'kubectl delete -f ./src/kube/scenario1.yml'
                sh 'kubectl delete -f ./src/kube/scenario2.yml'
                // we shouldn't have to clean the storage, just reuse it later
            }
        }
    }

    post {
        always {
            archiveArtifacts artifacts: 'build/libs/**/*.jar', fingerprint: true
            junit 'build/test-results/**/*.xml'
		    
		    // publish Test Coverage
		    publishHTML (target: [
		        allowMissing: false,
		        alwaysLinkToLastBuild: false,
		        keepAll: true,
		        reportDir: 'build/reports/jacocoHtml/',
		        reportFiles: 'index.html',
		        reportName: "Coverage Reports"
		    ])
        }
    }
}