import os
import json
import shutil
import argparse

def split_identifier(identifier):
    namespace, path = identifier.split(":")
    path_parts = path.split("/")
    advancement = path_parts[-1]
    tab = path_parts[-2] if len(path_parts) > 1 else ""
    return namespace, tab, advancement

def read_file_lines(file_path):
    with open(file_path, 'r', encoding='utf-8') as file:
        return file.readlines()

def find_first_matching_line(lines, substring):
    for line in lines:
        stripped_line = line.strip()
        if substring in stripped_line:
            return stripped_line[stripped_line.index(substring):].strip()
    return None

parser = argparse.ArgumentParser(description="Process JSON files in the specified folder.")
parser.add_argument("bacap_path", help="Path to the BACAP folder.")
args = parser.parse_args()

bacap_path = args.bacap_path

if not os.path.exists(bacap_path):
    raise FileNotFoundError(f"Path '{bacap_path}' does not exist.")

override_msg_path = "src/main/resources/resourcepacks/bacap_override/data/bacap_rewards/function/msg"

if os.path.exists(override_msg_path):
    shutil.rmtree(override_msg_path)

for root, _, files in os.walk(bacap_path):
    for file in files:
        if file.endswith('.json'):
            json_file = os.path.join(root, file)
            with open(json_file, 'r', encoding='utf-8') as file:
                content = file.read()
                content = content.replace("\\'", "\\\\'")
                data = json.loads(content)
                if 'display' in data and 'title' in data['display'] and 'rewards' in data and 'function' in data['rewards']:
                    reward_namespace, reward_tab, reward_advancement = split_identifier(data['rewards']['function'])
                    rewards_path = os.path.join(bacap_path, f"data/{reward_namespace}/function/{reward_tab}/{reward_advancement}.mcfunction")

                    if not os.path.exists(rewards_path):
                        continue

                    rewards_function = read_file_lines(rewards_path)
                    msg_function = find_first_matching_line(rewards_function, "bacap_rewards:msg")
                    if msg_function:
                        msg_namespace, msg_tab, msg_advancement = split_identifier(msg_function)
                        bacap_msg_path = os.path.join(bacap_path, f"data/{msg_namespace}/function/msg/{msg_tab}/{msg_advancement}.mcfunction")

                        if not os.path.exists(bacap_msg_path):
                            continue

                        advancement_namespace = os.path.basename(os.path.dirname(os.path.dirname(os.path.dirname(json_file))))
                        advancement_tab = os.path.basename(os.path.dirname(json_file))
                        advancement_name = os.path.splitext(os.path.basename(json_file))[0]
                        injection = f'"clickEvent":{{"action":"run_command","value":"/advancementssearch highlight {advancement_namespace}:{advancement_tab}/{advancement_name} obtained_status"}},'
                        with open(bacap_msg_path, 'r', encoding='utf-8') as file:
                            content = file.read()

                        updated_content = content.replace('"hoverEvent"', f'{injection}"hoverEvent"')
                        override_msg_function_path = os.path.join(override_msg_path, f"{msg_tab}/{msg_advancement}.mcfunction")
                        os.makedirs(os.path.dirname(override_msg_function_path), exist_ok=True)
                        with open(override_msg_function_path, 'w', encoding='utf-8') as file:
                            file.write(updated_content)
