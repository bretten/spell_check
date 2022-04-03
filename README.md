# How to run

- [Docker](##Docker)

## Docker
From the root of this repo, run:
```
docker build . -t spell-check-bn
docker run -dp 8080:8080 --name spell-check-bn spell-check-bn
```
This will create a tag named `spell-check-bn` and run a container named `spell-check-bn`.
