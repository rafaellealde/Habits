"""
Configuração centralizada do endpoint da API HabitFlow.

Em desenvolvimento local aponta para localhost:8080.
Em produção, define a variável de ambiente API_BASE_URL com a URL gerada
pela plataforma de nuvem (ex: https://habitflow-api.onrender.com).

Uso em qualquer módulo do frontend:
    from api_config import API_BASE_URL
    response = requests.get(f"{API_BASE_URL}/habits")
"""

import os

API_BASE_URL: str = os.environ.get("API_BASE_URL", "http://localhost:8080").rstrip("/")
