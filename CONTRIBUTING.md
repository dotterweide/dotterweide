# Contributing

We'd love for you to contribute and to improve this project!

As the project is only about to start, we have not strictly defined the guidelines for contribution yet.
This repository is about experimenting with Coursier and mainly contains soure code. Whether you wish to
contribute to this repository or start off another sub-project in the context of dotterweide, the best
is to talk to us on the Gitter channel.

The basic procedure should be that you fork the respective repository from the dotterweide organisation
to your personal GitHub account, create a new branch with a meaningful name (e.g. `topic-foo`), check in
your changes there and use the pull-request (PR) feature of GitHub (see below for pull requests).
Your PR will then be reviewed and either directly merged, or we propose some necessary changes.

If you find things that you feel you are not able to wrap in a pull request, please feel free to open
a corresponding issue on the issue tracker. Below are some guidelines for opening a successful ticket.

### Submitting an Issue

Before you submit your issue search the archive, maybe your question was already answered.

If your issue appears to be a bug, and hasn't been reported, open a new issue.
Help us to maximise the effort we can spend fixing issues and adding new
features, by not reporting duplicate issues. Providing the following information will increase the
chances of your issue being dealt with quickly:

- __Overview of the Issue__ - in the case of code, if an error is being thrown a non-minified stack trace helps.
  In the case of a text document, describe which document you are referring to.
- __Motivation for or Use Case__ - explain why this is an issue for you
- __Related Issues__ - has a similar issue been reported before?
- __Suggest a Fix__ - if you can't fix the issue yourself, perhaps you can point to what might be
  causing the problem (line of code or commit, or position in a text document)

__If you get help, help others. That way everyone benefits!__

### Submitting a Pull Request

Before you submit your pull request consider the following guidelines:

- Search the git repository for an open or closed Pull Request
  that relates to your submission. You don't want to duplicate effort.
- By submitting a pull request, you guarantee us that you are legally allowed
  to provide us with this request, i.e. that the requests contains your own
  original work and does not infringe on anybody else's copyright.
- Make your changes in a new git branch:

     ```shell
     git checkout -b issue-descriptive-name-branch master
     ```

- Create your patch (__including appropriate test cases__ if applicable).
- Commit your changes using a descriptive commit message that follows our
  [commit message conventions](#commit-message-format).

     ```shell
     git commit -a
     ```
  Note: the optional commit `-a` command line option will automatically "add" and "rm" edited files.

- Push your branch to the git hosting service (e.g. GitHub):

    ```shell
    git push origin issue-descriptive-name-branch
    ```

- In the git hosting service (e.g. GitHub), send a pull request to the upstream `work` branch, 
  if it exists, or to `master` branch if no `work` branch exists.
  If you are unsure about which branch to use as reference,
  consult with us first (through the issue tracker) to determine the best point of merge.
- If we suggest changes then:
    - Please make the required updates.
    - Commit your changes to your branch (e.g. `issue-descriptive-name-branch`).
    - Push the changes to your git repository on the hosting service (this will update your Pull Request).

If the PR gets too outdated we may ask you to rebase and force push to update the PR:

    ```shell
    git rebase work -i
    git push origin issue-descriptive-name-branch -f
    ```

_WARNING. Squashing or reverting commits and forced push thereafter may remove comments
on code on the git hosting service that were previously made by you and others in your commits._

That's it! Thank you for your contribution!

### After your pull request is merged

After your pull request is merged, you can safely delete your branch and pull the changes
from the main (upstream) repository:

- Delete the remote branch on the git hosting service (e.g. GitHub) either through its web UI or your local shell as follows:

    ```shell
    git push origin --delete issue-descriptive-name-branch
    ```

- Check out the `work` branch, or if it does not exist, the `master` branch (or the branch into which your work was merged):

    ```shell
    git checkout work -f
    ```

- Delete the local branch:

    ```shell
    git branch -D issue-descriptive-name-branch
    ```

- Update your `work` branch (or the branch into which your work was merged) with the latest upstream version:

    ```shell
    git pull --ff upstream work
    ```

## <a name="commit"></a> Git Commit Guidelines

### Commit Message Format

Use standard practice for the commit message. Use a subject
line no longer than 100 characters, followed by a body
outlining the details of the commit. If an issue in the issue
tracker exists, refer to it (e.g. `fixes #1234`). You can
also link to other commits by using the commit hash.

### Revert

If the commit reverts a previous commit, it should begin with `revert: `, followed by the header of the 
reverted commit. In the body it should say: `This reverts commit <hash>.`, where the hash is the SHA of the 
commit being reverted.

## <a name="info"></a> Further Information

When in doubt, please talk to us in the Gitter channel.
