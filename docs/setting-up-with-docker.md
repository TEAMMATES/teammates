# Starting Teammates Server with Docker

This is a step-by-step guide for starting Teammates in Docker. This is useful for testing the app without having to install all the prerequisites 

> If you encounter any problems during the setup process, please refer to our [troubleshooting guide](troubleshooting-guide.md) before posting a help request in our [issue tracker](https://github.com/TEAMMATES/teammates/issues).

## Step 1: Install necessary tools and languages

1. Install Git.
   1. (Optional but recommended) Install Sourcetree or other similar Git client.
1. Install Docker

## Step 2: Obtain your own copy of the repository

1. Fork our repo at https://github.com/TEAMMATES/teammates. Clone the fork to your hard disk.

1. Add a remote name (e.g `upstream`) for your copy of the main repo. Fetch the remote-tracking branches from the main repo to keep it in sync with your copy.
   ```sh
   git remote add upstream https://github.com/TEAMMATES/teammates.git
   git fetch upstream
   ```
   **Verification:** Use the command `git branch -r` and the following lines should be part of the output:
   ```
    upstream/master
    upstream/release
    ```

1. Set your `master` branch to track the main repo's `master` branch.
   ```sh
   git checkout master
   git branch -u upstream/master
   ```

More information can be found at [this documentation](https://help.github.com/articles/fork-a-repo/).

## Step 3: Start the application container

Run this command to start the application container:
   ```sh
   docker-compose up
   ```
   **Verification:** System will be accessible at http://localhost:8080.                         


To recreate the docker images and containers (e.g: following a code change), use the following command:
   ```sh
   docker-compose up --build --force-recreate
   ```
   **Note:** Recreating the container will delete it and the datastore.
