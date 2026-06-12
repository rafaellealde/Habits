import requests
from core.config import API_URL

def get_habits():
    try:
        resp = requests.get(f"{API_URL}/habits", timeout=5)
        if resp.status_code == 200:
            return resp.json(), None
        return None, f"Erro: {resp.text}"
    except Exception as e:
        return None, str(e)

def get_habit(habit_id):
    try:
        resp = requests.get(f"{API_URL}/habits/{habit_id}", timeout=5)
        if resp.status_code == 200:
            return resp.json(), None
        return None, f"Erro: {resp.text}"
    except Exception as e:
        return None, str(e)

def create_habit(data):
    try:
        resp = requests.post(f"{API_URL}/habits", json=data, timeout=5)
        if resp.status_code == 201:
            return True, None
        return False, f"Erro: {resp.text}"
    except Exception as e:
        return False, str(e)

def update_habit(habit_id, data):
    try:
        resp = requests.put(f"{API_URL}/habits/{habit_id}", json=data, timeout=5)
        if resp.status_code == 200:
            return True, None
        return False, f"Erro: {resp.text}"
    except Exception as e:
        return False, str(e)

def delete_habit(habit_id):
    try:
        resp = requests.delete(f"{API_URL}/habits/{habit_id}", timeout=5)
        if resp.status_code == 204:
            return True, None
        return False, f"Erro: {resp.text}"
    except Exception as e:
        return False, str(e)
