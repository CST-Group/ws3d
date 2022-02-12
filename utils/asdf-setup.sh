#!/usr/bin/sh

asdf plugin-add java https://github.com/halcyon/asdf-java.git
asdf plugin-add gradle https://github.com/rfrancis/asdf-gradle.git
asdf install
gradle run
