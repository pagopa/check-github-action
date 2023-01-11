export default function checkSize() {
  const additions = context.payload.pull_request.additions || 0
  const deletions = context.payload.pull_request.deletions || 0
  var changes = additions + deletions

  const { IGNORED_FILES } = process.env
  const ignored_files = IGNORED_FILES.trim().split(',').filter(word => word.length > 0);
  if (ignored_files.length > 0){
    var ignored = 0
    const execSync = require('child_process').execSync;
    for (const file of IGNORED_FILES.trim().split(',')) {

      const ignored_additions = execSync('git --no-pager  diff --numstat | grep ' + file + ' | cut -f 1', { encoding: 'utf-8' })
      const ignored_deletions = execSync('git --no-pager  diff --numstat | grep ' + file + ' | cut -f 2', { encoding: 'utf-8' })

      ignored += (parseInt(ignored_additions || 0) + parseInt(ignored_deletions || 0))
    }
    changes -= ignored
  }

  if (changes < 200){
    github.rest.issues.addLabels({
      issue_number: context.issue.number,
      owner: context.repo.owner,
      repo: context.repo.repo,
      labels: ['size/small']
    })
  }

  if (changes > 400){
    github.rest.issues.addLabels({
      issue_number: context.issue.number,
      owner: context.repo.owner,
      repo: context.repo.repo,
      labels: ['size/large']
    })
    github.rest.issues.createComment({
      issue_number: context.issue.number,
      owner: context.repo.owner,
      repo: context.repo.repo,
      body: 'This PR exceeds the recommended size of 400 lines. Please make sure you are NOT addressing multiple issues with one PR. _Note this PR might be rejected due to its size._'
    })
    core.setFailed('PR is too large')
  }
}
