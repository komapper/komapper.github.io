# Komapper Documentation

[![Netlify Status](https://api.netlify.com/api/v1/badges/ec21695f-242f-43af-8a30-2d13a84f0637/deploy-status)](https://app.netlify.com/sites/komapper/deploys)

## Deployment site

https://www.komapper.org/

## Contributing to these docs

English version:
https://www.komapper.org/docs/contribution-guidelines/#contributing-to-these-docs

日本語版:
https://www.komapper.org/ja/docs/contribution-guidelines/#contributing-to-these-docs

## Release steps

### in the main branch

1. Change version numbers in gradle.properties
2. Execute `./gradlew updateVersion`
3. Add new version and url in config.toml
4. Commit changes
5. Create a new branch from the main branch
6. Change the new branch to a production branch on the Netlify page
7. Push main and the new branches to remote

### in the old branch

1. Execute `./gradlew archive`
2. Push the branch to remote