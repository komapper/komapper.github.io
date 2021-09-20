---
title: "Contribution Guidelines"
linkTitle: "Contribution Guidelines"
weight: 500
description: >
  How to contribute to Komapper
---

## Overview

Komapper is an open source project and we love getting patches and contributions to make Komapper and its docs even better.

## Contributing to Komapper

The Komapper source code lives in <https://github.com/komapper/komapper>.

### Code reviews

All submissions, including submissions by project members, require review. We
use GitHub pull requests for this purpose. Consult
[GitHub Help](https://help.github.com/articles/about-pull-requests/) for more
information on using pull requests.

### Creating issues

Alternatively, if there's something you'd like to see in Komapper (or if you've found something that isn't working the way you'd expect), but you're not sure how to fix it yourself, please create an [issue](https://github.com/komapper/komapper/issues).

## Contributing to these docs

This documentation is a Komapper site that uses the Hugo static site generator. We welcome updates to the docs!

We use GitHub Pages to manage the deployment of the site. The instructions here assume you're familiar with basic GitHub workflows.

### Quick start

1. Fork the [Komapper documentation repo](https://github.com/komapper/komapper.github.io) on GitHub.
1. Make your changes and send a pull request (PR).

### Updating a single page

If you've just spotted something you'd like to change while using the docs, we provide a shortcut for you:

1. Click **Edit this page** in the top right hand corner of the page.
1. If you don't already have an up to date fork of the project repo, you are prompted to get one - click **Fork this repository and propose changes** or **Update your Fork** to get an up to date version of the project to edit. The appropriate page in your fork is displayed in edit mode.
1. Make your changes and send a pull request (PR).

### Previewing your changes locally

If you want to run your own local Hugo server to preview your changes as you work:

1. Fork the [Komapper documentation repo](https://github.com/komapper/komapper.github.io) into your own project, then create a local copy using `git clone`. Don’t forget to use `--recurse-submodules` or you won’t pull down some of the code you need to generate a working site.

    ```
    git clone --recurse-submodules --depth 1 https://github.com/komapper/komapper.github.io.git
    ```

1. Change to the `komapper.github.io` directory and run the following docker command to build the site and start the Hugo server.

    ```
    docker compose up
    ```
    
    By default your site will be available at <http://localhost:1313/>. Now that you're serving your site locally, Hugo will watch for changes to the content and automatically refresh your site.
   
1. Continue with the usual GitHub workflow to edit files, commit them, push the
  changes up to your fork, and create a pull request.

### Creating an issue

If there's something you'd like to see in the docs, but you're not sure how to fix it yourself, please create an issue in [this repository](https://github.com/komapper/komapper.github.io). You can also create an issue about a specific page by clicking the **Create documentation issue** link in the top right hand corner of the page.


