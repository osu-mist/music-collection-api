# Music Collection API

Keep track of your albums on a virtual music shelf.

### Generate Keys

HTTPS is required for Web APIs in development and production. Use `keytool(1)` to generate public and private keys.

Generate key pair and keystore:

    $ keytool \
        -genkey \
        -dname "CN=Jane Doe, OU=Enterprise Computing Services, O=Oregon State University, L=Corvallis, S=Oregon, C=US" \
        -alias "doej" \
        -keyalg "RSA" \
        -keysize 2048 \
        -validity 365 \
        -keystore doej.keystore

Create self-signed certificate:

    $ keytool \
        -selfcert \
        -alias "doej" \
        -sigalg "SHA256withRSA" \
        -keystore doej.keystore

Export certificate to file:

    $ keytool \
        -export \
        -alias "doej" \
        -keystore doej.keystore \
        -file doej.cer

Import certificate into truststore:

    $ keytool \
        -import \
        -alias "doej" \
        -file doej.cer \
        -keystore doej.truststore

## Gradle

This project uses the build automation tool [Gradle][].

[Gradle]: https://gradle.org/

## Tasks

List all tasks runnable from root project:

    $ gradle tasks

### IntelliJ IDEA

Generate IntelliJ IDEA project:

    $ gradle idea

Open with `File` -> `Open Project`.

### Configure

Copy [configuration-example.yaml](configuration-example.yaml) to `configuration.yaml`. Modify as necessary, being careful to avoid committing sensitive data.

### Build

Build the project:

    $ gradle build

JARs [will be saved](https://github.com/johnrengelman/shadow#using-the-default-plugin-task) into the directory `build/libs/`.

### Run

Run the project:

    $ gradle run


## Resources

The Web API definition is contained in the [Swagger specification](swagger.yaml).

The following examples demonstrate the use of `curl` to make authenticated HTTPS requests.

### GET /

This resource returns build and runtime information:

    $ curl \
    > --insecure \
    > --key doej.cer \
    > --user "username:password" \
    > https://localhost:8080/api/v0/
    {"name":"music-collection-api","time":1464809765774,"commit":"348534d","documentation":"swagger.yaml"}

### GET /sample

This sample resource returns a short message:

    $ curl \
    > --insecure \
    > --key doej.cer \
    > --user "username:password" \
    > https://localhost:8080/api/v0/sample
    hello world

### POST /sample

This sample resource returns the request message:

    $ curl \
    > --insecure \
    > --key doej.cer \
    > --user "username:password" \
    > --header "Content-Type: text/plain" \
    > --data "goodbye world" \
    > https://localhost:8080/api/v0/sample
    goodbye world
