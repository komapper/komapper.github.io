# Komapper Documentation

[![Netlify Status](https://api.netlify.com/api/v1/badges/ec21695f-242f-43af-8a30-2d13a84f0637/deploy-status)](https://app.netlify.com/sites/komapper/deploys)

## Deployment site

https://www.komapper.org/

## Contributing to these docs

See [CONTRIBUTING.md](CONTRIBUTING.md).

## Release steps

### in the main branch

1. Change version numbers in gradle.properties
2. Execute `./gradlew updateVersion`
3. Add new version and url in config.toml
4. Change the url of old version in config.toml
5. Commit changes
6. Create a new branch from the main branch
7. Change the new branch to a production branch on the Netlify page
8. Push main and the new branches to remote

### in the old branch

1. Execute `./gradlew archive`
2. Change the url and add the latest url in config.toml
3. Commit changes
4. Push the branch to remote
5. Create a new subdomain for the old branch on the Netlify page
