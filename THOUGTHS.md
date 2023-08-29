# thoughts

## updates

1. Should create PhraseDefinition and define fields
2. I think about to extract as a separate module all interactions - and remain domain eng module not dependent to remote
   services like yandex, dictionary and etc

## eng

The component ```eng``` is the core of my own platform, it should consist domain logic.

Main parts are:

1. Word storage (simple CRUD in here, store, enrich) + events subsystem (at least just my will)
2. Word grouping (tags, groups, metrics)
3. Flexible revise algorithm
4. Direct translation to Russian (should I do it choosable?)

## integrations

In here I have:

1. All data for a word from urbandictionary (need to find out how to clarify if the passed word is for urban) - 
   just to avoid redundant api calls.
2. All meanings for a word from dictionary, pronunciation and synonyms
3. Translation for a word from yandex cloud

## connectors

As separate modules, should be three in the start:

1. Telegram bot - entrypoint for interacting with core (the first UI)
2. Typora importer - scan data from my own md files and push them
3. UI - a website based on mkdocs with feature can be found in Github as plugins

## important commands
set -U fish_user_paths $PATH_TO_BIN $fish_user_paths
