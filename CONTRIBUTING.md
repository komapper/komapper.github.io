# Welcome to Komapper documentation contributing guide

We appreciate your interest in contributing to the Komapper documentation!
This project thrives on the support of the community, and your contributions are essential.
This document provides guidelines for those who wish to improve or expand the Komapper documentation.

We use [Netlify](https://www.netlify.com/) to manage the deployment of the site and provide previews of doc updates.
The instructions here assume you're familiar with basic GitHub workflows.

## Quick start with Netlify

1. Fork the komapper-docs repository on GitHub.
2. Make your changes and send a pull request (PR).
3. If you're not yet ready for a review, add "WIP" to the PR name to indicate it's a work in progress.
4. Wait for the automated PR workflow to do some checks. When it's ready,
   you should see a comment like this: **Deploy Preview for komapper ready!**.
5. Click the link to the right of "Deploy preview" to see a preview of your updates.
6. Continue updating your doc and pushing your changes until you're happy with the content.
7. When you're ready for a review, add a comment to the PR, and remove any "WIP" markers.

## Updating a single page

If you've just spotted something you'd like to change while using the docs, there is a shortcut for you:

1. Click **Edit this page** in the top right hand corner of the page.
2. If you don't already have an up to date fork of the project repo, 
   you are prompted to get one - click **Fork this repository and propose changes** or 
   **Update your Fork** to get an up to date version of the project to edit.
   The appropriate page in your fork is displayed in edit mode.
3. Follow the rest of the [Quick start with Netlify](#quick-start-with-netlify) 
   process above to make and preview your changes.

## Previewing your changes locally

### Build and run the container

The komapper-docs repository includes a
[Dockerfile](https://docs.docker.com/engine/reference/builder/).

1. Build the docker image:

   ```bash
   docker compose build
   ```

2. Run the built image:

   ```bash
   docker compose up
   ```

3. Open the address `http://localhost:1313` in your web browser to load the
   komapper-docs site. You can now make changes to the source files, those
   changes will be live-reloaded in your browser.

### Cleanup

To cleanup your system and delete the container image follow the next steps.

1. Stop Docker Compose with **Ctrl + C**.

2. Remove the produced images

   ```bash
   docker compose rm
   ```
