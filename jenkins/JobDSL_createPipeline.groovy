// Job DSL script to create a Pipeline job that uses `Jenkinsfile.withMaven` from SCM
// Usage:
// 1) Replace `repoUrl` and `credentialsId` with your repository URL and optional credentials ID.
// 2) Create a Seed job in Jenkins (Freestyle or Pipeline) with the Job DSL plugin and run this script.
// 3) The script will create a job named `testNgframeworkwithAIV1-pipeline`.

def repoUrl = 'https://github.com/your-org/your-repo.git' // <-- REPLACE with your repo URL
def credentialsId = 'git-credentials-id' // <-- REPLACE or set to null if repo is public

def jobName = 'testNgframeworkwithAIV1-pipeline'

pipelineJob(jobName) {
  description('Pipeline job created by Job DSL - uses Jenkinsfile.withMaven in repository root')
  definition {
    cpsScm {
      scm {
        git {
          remote {
            url(repoUrl)
            if (credentialsId && credentialsId.trim()) {
              credentials(credentialsId)
            }
          }
          branches('*/main')
          extensions {}
        }
      }
      scriptPath('Jenkinsfile.withMaven')
    }
  }

  triggers {
    scm('H/5 * * * *')
  }

  logRotator {
    daysToKeep(30)
    numToKeep(10)
  }
}
