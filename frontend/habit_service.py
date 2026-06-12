"""
Camada de acesso à API HabitFlow.
Isola as chamadas HTTP do restante da UI Flet — a view não conhece a URL nem o requests.
"""

import requests
from api_config import API_BASE_URL


class HabitService:

    def get_all(self) -> list[dict]:
        response = requests.get(f"{API_BASE_URL}/habits", timeout=10)
        response.raise_for_status()
        return response.json()

    def get_by_id(self, habit_id: int) -> dict:
        response = requests.get(f"{API_BASE_URL}/habits/{habit_id}", timeout=10)
        response.raise_for_status()
        return response.json()

    def create(self, name: str, description: str, frequency: str) -> dict:
        payload = {"name": name, "description": description, "frequency": frequency}
        response = requests.post(f"{API_BASE_URL}/habits", json=payload, timeout=10)
        response.raise_for_status()
        return response.json()

    def update(self, habit_id: int, name: str, description: str, frequency: str) -> dict:
        payload = {"name": name, "description": description, "frequency": frequency}
        response = requests.put(f"{API_BASE_URL}/habits/{habit_id}", json=payload, timeout=10)
        response.raise_for_status()
        return response.json()

    def delete(self, habit_id: int) -> None:
        response = requests.delete(f"{API_BASE_URL}/habits/{habit_id}", timeout=10)
        response.raise_for_status()
