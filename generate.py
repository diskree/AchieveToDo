import os
import json

def split_identifier(identifier):
    namespace, path = identifier.split(":")
    path_parts = path.split("/")
    advancement = path_parts[-1]
    tab = "/".join(path_parts[:-1]) if len(path_parts) > 1 else ""
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

folder_path = "E:/BACAP"

# Лог ошибок
log_file = "missing_files.log"

# Создаём лог-файл (очищаем старое содержимое)
with open(log_file, 'w', encoding='utf-8') as log:
    log.write("Missing Files Log:\n")

for root, _, files in os.walk(folder_path):
    for file in files:
        if file.endswith('.json'):
            json_file = os.path.join(root, file)
            try:
                with open(json_file, 'r', encoding='utf-8') as file:
                    data = json.load(file)
                    if 'display' in data and 'title' in data['display'] and 'rewards' in data and 'function' in data['rewards']:
                        advancement_namespace = os.path.basename(os.path.dirname(os.path.dirname(os.path.dirname(json_file))))
                        advancement_tab = os.path.basename(os.path.dirname(json_file))
                        advancement = os.path.basename(json_file)

                        namespace, tab, advancement = split_identifier(data['rewards']['function'])
                        rewards_path = os.path.join(folder_path, f"data/{namespace}/function/{tab}/{advancement}.mcfunction")
                        
                        if not os.path.exists(rewards_path):
                            # Логируем отсутствие файла
                            with open(log_file, 'a', encoding='utf-8') as log:
                                log.write(f"Missing file: {rewards_path}\n")
                            continue

                        rewards_function = read_file_lines(rewards_path)
                        msg_function = find_first_matching_line(rewards_function, "bacap_rewards:msg")
                        if msg_function:
                            namespace, tab, advancement = split_identifier(msg_function)
                            msg_path = os.path.join(folder_path, f"data/{namespace}/function/{tab}/{advancement}.mcfunction")
                            
                            if not os.path.exists(msg_path):
                                # Логируем отсутствие файла
                                with open(log_file, 'a', encoding='utf-8') as log:
                                    log.write(f"Missing file: {msg_path}\n")
                                continue
                            
                            injection = f'"clickEvent":{{"action":"run_command", "value":"/advancementssearch highlight {advancement_namespace}:{advancement_tab}/{advancement} obtained_status"}},'
                            with open(msg_path, 'r', encoding='utf-8') as file:
                                content = file.read()
                            updated_content = content.replace('"hoverEvent"', f'{injection}"hoverEvent"')
                            with open(msg_path, 'w', encoding='utf-8') as file:
                                file.write(updated_content)
            except FileNotFoundError:
                # Логируем общий случай, если файл не найден
                with open(log_file, 'a', encoding='utf-8') as log:
                    log.write(f"File not found: {json_file}\n")
            except Exception as e:
                # Логируем любые другие ошибки
                with open(log_file, 'a', encoding='utf-8') as log:
                    log.write(f"Error processing file {json_file}: {e}\n")
