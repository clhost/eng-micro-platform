#!/bin/bash

# Text colorization/customization
NC='\033[0m'
YELLOW='\033[0;33m'
LIGHT_RED='\033[0;91m'
BOLD='\033[1m'

# Global variables
ENGP_DIR="$HOME/.engp"

# Local variables
engp_version="dev"
engp_bin_folder="${ENGP_DIR}/bin"
engp_src_folder="${ENGP_DIR}/src"
engp_config_file="${ENGP_DIR}/config"
engp_version_file="${ENGP_DIR}/version"

engp_bash_completions_file="$engp_src_folder/engp-bash-completions.sh"
engp_zsh_completions_file="$engp_src_folder/engp-zsh-completions.sh"
engp_fish_completions_file="$engp_src_folder/engp-completions.fish"

bashrc_file="${HOME}/.bashrc"
bash_profile_file="${HOME}/.bash_profile"
zshrc_file="${ZDOTDIR:-${HOME}}/.zshrc"
fish_completions_folder="${HOME}/.config/fish/completions"
fish_conf_folder="${HOME}/.config/fish/conf.d"
engp_fish_conf_file="${HOME}/.config/fish/conf.d/engp.fish"

# Arguments
argument="$1"

# Snippets
engp_bash_completion_snippet=$(cat << EOF
export PATH="$engp_bin_folder:\$PATH"
if [[ \$SHELL == *"bash"* ]] && [[ -f $engp_bash_completions_file ]]; then
    source ${engp_bash_completions_file}
fi
EOF
)

engp_zsh_completion_snippet=$(cat << EOF
export PATH="$engp_bin_folder:\$PATH"
if [[ \$SHELL == *"zsh"* ]] && [[ -f $engp_zsh_completions_file ]]; then
    source ${engp_zsh_completions_file}
fi
EOF
)

engp_fish_path_snippet=$(cat << EOF
set -U fish_user_paths $engp_bin_folder \$fish_user_paths
EOF
)

engp_default_config=$(cat << EOF
repositoryFolder: "$HOME/idea-projects/eng-micro-platform"
EOF
)

echo "A CLI tool that helps to work with local development envitonment"
echo ""
echo "Now attempting installation..."

echo "Looking for unzip..."
if ! command -v unzip > /dev/null; then
    echo "Not found."
    echo ""
    echo "====================================================================================="
    echo " Please install unzip on your system using your favourite package manager."
    echo ""
    echo " Restart after installing unzip."
    echo "====================================================================================="
    echo ""
    exit 1
fi

echo ""
echo "Remove previous installation..."
rm -rf $ENGP_DIR

echo -e "\nBuilding native image..."
if [ ! -f "build/native/engp" ] || [ "$argument" == "rebuild" ]; then
  cd ../../
  ./gradlew :engp:clean :engp:nativeImage
  cd tools/engp
fi

echo -e "\nCreate distribution directories..."
mkdir -p "$engp_bin_folder" && echo "$engp_bin_folder"
mkdir -p "$engp_src_folder" && echo "$engp_src_folder"

echo -e "\nMove engp to bin..."
chmod +x "build/native/engp"
cp "build/native/engp" "$engp_bin_folder"

echo -e "\nUnzip engp sources to src..."
rm -rf $engp_src_folder/*
unzip "build/sources/sources.zip" -d "$engp_src_folder"

echo -e "\nSet version to $engp_version ..."
echo "$engp_version" > "${engp_version_file}"
echo ""

echo -e "\nPrime the config file..."
touch "$engp_config_file"
echo "$engp_default_config" > "$engp_config_file"

echo -e "\nGenerate completion scripts..."

echo "" > "$engp_bash_completions_file"
echo "" > "$engp_zsh_completions_file"
echo "" > "$engp_fish_completions_file"

"${engp_bin_folder}"/engp --generate-completion bash > "$engp_bash_completions_file"
"${engp_bin_folder}"/engp --generate-completion zsh > "$engp_zsh_completions_file"
"${engp_bin_folder}"/engp --generate-completion fish > "$engp_fish_completions_file"

if [[ $(uname) == "Darwin" ]]; then
    touch "$bash_profile_file"
    echo "Attempt update of login bash profile on OSX..."
    if [[ -z $(grep 'engp-bash-completions.sh' "$bash_profile_file") ]]; then
        echo -e "\n$engp_bash_completion_snippet" >> "$bash_profile_file"
        echo "Added engp completion init snippet to $bash_profile_file"
        echo ""
    fi
fi

if [[ $(uname) != "Darwin" ]]; then
    echo "Attempt update of interactive bash profile on regular UNIX..."
    touch "$bashrc_file"
    if [[ -z $(grep 'engp-bash-completions.sh' "$bashrc_file") ]]; then
        echo -e "\n$engp_bash_completion_snippet" >> "$bashrc_file"
        echo "Added engp completion init snippet to $bashrc_file"
        echo ""
    fi
fi

if [ -f "$zshrc_file" ]; then
    echo "Attempt update of zsh profile..."
    touch "$zshrc_file"
    if [[ -z $(grep 'engp-zsh-completions.sh' "$zshrc_file") ]]; then
        echo -e "\n$engp_zsh_completion_snippet" >> "$zshrc_file"
        echo "Added engp completion init snippet to $zshrc_file"
        echo ""
    fi
fi

if ! [ -d "$fish_completions_folder" ]; then
    echo "Create folder $fish_completions_folder"
    mkdir "$fish_completions_folder"
    echo ""
fi

if [ -d "$fish_completions_folder" ]; then
    echo "Attempt update of fish..."
    touch "$engp_fish_conf_file"
    if [[ -z $(grep 'set -U fish_user_paths '$engp_bin_folder'' "$engp_fish_conf_file") ]]; then
        echo -e "$engp_fish_path_snippet" >> "$engp_fish_conf_file"
        echo "Added engp path init snippet to $engp_fish_conf_file"
        echo ""
    fi
    ln -sf "$engp_fish_completions_file" "$fish_completions_folder/engp.fish"
    echo ""
fi

echo -e "\nAll done!\n\n"

echo "Please open a new terminal and issue the following command:"
echo ""
echo "    engp"
echo ""
echo "Enjoy!!!"
