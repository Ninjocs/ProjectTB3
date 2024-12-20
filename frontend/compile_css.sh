#!/bin/bash


if [ -x "$(command -v sass)" ]; then
  SASS_EXEC=sass
fi


if [ -z "$SASS_EXEC" ]; then
  echo "You need to have SASS installed"
  exit 1
fi

CHOKIDAR_USEPOLLING=true sass --watch integration/style.scss integration/assets/stylesheets/output.css
