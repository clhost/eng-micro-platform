#!/bin/bash

# Text colorization/customization
NC='\033[0m'
YELLOW='\033[0;33m'
LIGHT_RED='\033[0;91m'
BOLD='\033[1m'

# Global variables
ENG_DIR="$HOME/.eng"

# Local variables
eng_version="dev"
eng_bin_folder="${ENG_DIR}/bin"
eng_src_folder="${ENG_DIR}/src"
eng_config_file="${ENG_DIR}/config"
eng_version_file="${ENG_DIR}/version"

eng_bash_completions_file="$eng_src_folder/eng-bash-completions.sh"
eng_zsh_completions_file="$eng_src_folder/eng-zsh-completions.sh"
eng_fish_completions_file="$eng_src_folder/eng-completions.fish"

bashrc_file="${HOME}/.bashrc"
bash_profile_file="${HOME}/.bash_profile"
zshrc_file="${ZDOTDIR:-${HOME}}/.zshrc"
fish_completions_folder="${HOME}/.config/fish/completions"
fish_conf_folder="${HOME}/.config/fish/conf.d"
eng_fish_conf_file="${HOME}/.config/fish/conf.d/eng.fish"

ENG_MICRO_PLATFORM_URL="http://localhost:11111"

if command -v minikube > /dev/null; then
  MINIKUBE_STATUS=$(minikube status | grep "host:" | cut -d " " -f 2)
  if [[ $MINIKUBE_STATUS == "Running" ]]; then
    ENG_MICRO_PLATFORM_HOST=http://$(minikube ip):32000
  fi
fi

# Arguments
argument="$1"

# Snippets
eng_bash_completion_snippet=$(cat << EOF
export PATH="$eng_bin_folder:\$PATH"
if [[ \$SHELL == *"bash"* ]] && [[ -f $eng_bash_completions_file ]]; then
    source ${eng_bash_completions_file}
fi
EOF
)

eng_zsh_completion_snippet=$(cat << EOF
export PATH="$eng_bin_folder:\$PATH"
if [[ \$SHELL == *"zsh"* ]] && [[ -f $eng_zsh_completions_file ]]; then
    source ${eng_zsh_completions_file}
fi
EOF
)

eng_fish_path_snippet=$(cat << EOF
set -U fish_user_paths $eng_bin_folder \$fish_user_paths
EOF
)

eng_default_config=$(cat << EOF
engMicroPlatformUrl: "$ENG_MICRO_PLATFORM_URL"
EOF
)

echo "A CLI tool to interact with eng-micro-platform"
echo ""
echo "Now attempting installation..."

echo ""
echo "Remove previous installation..."
rm -rf $ENG_DIR

echo -e "\nBuilding native image..."
if [ ! -f "build/native/eng" ] || [ "$argument" == "rebuild" ]; then
  cd ../../
  ./gradlew :eng-cli:clean :eng-cli:nativeImage
  cd platform/eng-cli
fi

echo -e "\nCreate distribution directories..."
mkdir -p "$eng_bin_folder" && echo "$eng_bin_folder"
mkdir -p "$eng_src_folder" && echo "$eng_src_folder"

echo -e "\nMove eng to bin..."
chmod +x "build/native/eng"
cp "build/native/eng" "$eng_bin_folder"

echo -e "\nSet version to $eng_version ..."
echo "$eng_version" > "${eng_version_file}"
echo ""

echo -e "\nPrime the config file..."
touch "$eng_config_file"
echo "$eng_default_config" > "$eng_config_file"

echo -e "\nGenerate completion scripts..."

echo "" > "$eng_bash_completions_file"
echo "" > "$eng_zsh_completions_file"
echo "" > "$eng_fish_completions_file"

"${eng_bin_folder}"/eng --generate-completion bash > "$eng_bash_completions_file"
"${eng_bin_folder}"/eng --generate-completion zsh > "$eng_zsh_completions_file"
"${eng_bin_folder}"/eng --generate-completion fish > "$eng_fish_completions_file"

if [[ $(uname) == "Darwin" ]]; then
    touch "$bash_profile_file"
    echo "Attempt update of login bash profile on OSX..."
    if [[ -z $(grep 'eng-bash-completions.sh' "$bash_profile_file") ]]; then
        echo -e "\n$eng_bash_completion_snippet" >> "$bash_profile_file"
        echo "Added eng completion init snippet to $bash_profile_file"
        echo ""
    fi
fi

if [[ $(uname) != "Darwin" ]]; then
    echo "Attempt update of interactive bash profile on regular UNIX..."
    touch "$bashrc_file"
    if [[ -z $(grep 'eng-bash-completions.sh' "$bashrc_file") ]]; then
        echo -e "\n$eng_bash_completion_snippet" >> "$bashrc_file"
        echo "Added eng completion init snippet to $bashrc_file"
        echo ""
    fi
fi

if [ -f "$zshrc_file" ]; then
    echo "Attempt update of zsh profile..."
    touch "$zshrc_file"
    if [[ -z $(grep 'eng-zsh-completions.sh' "$zshrc_file") ]]; then
        echo -e "\n$eng_zsh_completion_snippet" >> "$zshrc_file"
        echo "Added eng completion init snippet to $zshrc_file"
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
    touch "$eng_fish_conf_file"
    if [[ -z $(grep 'set -U fish_user_paths '$eng_bin_folder'' "$eng_fish_conf_file") ]]; then
        echo -e "$eng_fish_path_snippet" >> "$eng_fish_conf_file"
        echo "Added eng path init snippet to $eng_fish_conf_file"
        echo ""
    fi
    ln -sf "$eng_fish_completions_file" "$fish_completions_folder/eng.fish"
    echo ""
fi

echo -e "\nAll done!\n\n"

echo "Please open a new terminal and issue the following command:"
echo ""
echo "    eng"
echo ""
echo "Enjoy!!!"
