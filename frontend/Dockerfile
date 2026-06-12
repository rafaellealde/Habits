FROM python:3.13-slim

WORKDIR /app

COPY requirements.txt .
RUN pip install --no-cache-dir -r requirements.txt

COPY . .

# PORT é injetada pelo Render em runtime; 8550 é o fallback local
EXPOSE 8550

CMD ["python", "main.py"]
