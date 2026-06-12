import os
import requests
import flet as ft

# =====================================================================
# CONFIGURAÇÃO CENTRALIZADA DE ROTAS E API
# Em desenvolvimento local, o Flet deve apontar para http://localhost:8080.
# Em produção, a nuvem injetará a variável API_URL via ambiente.
# =====================================================================
API_URL = os.getenv("API_URL", "http://localhost:8080")

def main(page: ft.Page):
    page.title = "Gerenciador de Hábitos (HabitFlow)"
    page.theme_mode = ft.ThemeMode.DARK
    page.padding = 30
    page.window_width = 600
    page.window_height = 800

    # UI Elements
    habits_list_view = ft.ListView(expand=True, spacing=10)
    
    name_input = ft.TextField(label="Nome do Hábito", expand=True)
    desc_input = ft.TextField(label="Descrição", expand=True)
    freq_dropdown = ft.Dropdown(
        label="Frequência",
        options=[
            ft.dropdown.Option("DAILY", "Diário"),
            ft.dropdown.Option("WEEKLY", "Semanal"),
            ft.dropdown.Option("MONTHLY", "Mensal"),
        ],
        value="DAILY",
        width=150
    )

    def show_message(message, is_error=False):
        color = ft.Colors.ERROR if is_error else ft.Colors.GREEN
        page.snack_bar = ft.SnackBar(ft.Text(message), bgcolor=color)
        page.snack_bar.open = True
        page.update()

    def load_habits():
        habits_list_view.controls.clear()
        try:
            # Faz a leitura do endpoint do backend
            response = requests.get(f"{API_URL}/habits", timeout=5)
            if response.status_code == 200:
                habits = response.json()
                if not habits:
                    habits_list_view.controls.append(ft.Text("Nenhum hábito cadastrado ainda. Comece agora!"))
                    
                for habit in habits:
                    habit_id = habit.get("id")
                    name = habit.get("name", "Sem Nome")
                    desc = habit.get("description", "")
                    freq = habit.get("frequency", "")
                    
                    habit_row = ft.ListTile(
                        leading=ft.Icon("check_circle_outline", color=ft.Colors.BLUE_400),
                        title=ft.Text(name, weight=ft.FontWeight.BOLD),
                        subtitle=ft.Text(f"{desc} | Frequência: {freq}"),
                        trailing=ft.IconButton(
                            icon="delete",
                            icon_color=ft.Colors.RED_400,
                            tooltip="Excluir hábito",
                            data=habit_id,
                            on_click=delete_habit
                        )
                    )
                    habits_list_view.controls.append(habit_row)
            else:
                show_message(f"Erro ao carregar hábitos: {response.text}", is_error=True)
        except requests.exceptions.ConnectionError:
            show_message(f"Falha de conexão: O Backend não está rodando em {API_URL}", is_error=True)
        except Exception as e:
            show_message(f"Erro inesperado: {e}", is_error=True)
        
        page.update()

    def add_habit(e):
        if not name_input.value:
            show_message("O nome do hábito é obrigatório.", is_error=True)
            return
            
        payload = {
            "name": name_input.value,
            "description": desc_input.value,
            "frequency": freq_dropdown.value
        }
        
        try:
            response = requests.post(f"{API_URL}/habits", json=payload, timeout=5)
            if response.status_code == 201:
                name_input.value = ""
                desc_input.value = ""
                freq_dropdown.value = "DAILY"
                show_message("Hábito criado com sucesso!")
                load_habits()
            else:
                show_message(f"Erro ao criar hábito: {response.text}", is_error=True)
        except Exception as ex:
            show_message(f"Erro na requisição: {ex}", is_error=True)

    def delete_habit(e):
        habit_id = e.control.data
        if not habit_id:
            return
            
        try:
            response = requests.delete(f"{API_URL}/habits/{habit_id}", timeout=5)
            if response.status_code == 204:
                show_message("Hábito excluído.")
                load_habits()
            else:
                show_message(f"Erro ao deletar: {response.text}", is_error=True)
        except Exception as ex:
            show_message(f"Erro na requisição: {ex}", is_error=True)

    # Header
    header = ft.Row([
        ft.Icon("star", color=ft.Colors.YELLOW),
        ft.Text("HabitFlow - Frontend", style=ft.TextThemeStyle.HEADLINE_MEDIUM, weight=ft.FontWeight.BOLD)
    ], alignment=ft.MainAxisAlignment.CENTER)

    # Form
    input_row = ft.Row([name_input, desc_input, freq_dropdown], alignment=ft.MainAxisAlignment.SPACE_BETWEEN)
    add_button = ft.ElevatedButton("Adicionar Hábito", icon="add", on_click=add_habit, expand=True)

    page.add(
        header,
        ft.Divider(height=20, color=ft.Colors.TRANSPARENT),
        ft.Text("Cadastrar Novo Hábito", style=ft.TextThemeStyle.TITLE_MEDIUM),
        input_row,
        ft.Row([add_button]),
        ft.Divider(height=30),
        ft.Text("Meus Hábitos", style=ft.TextThemeStyle.TITLE_LARGE),
        habits_list_view
    )
    
    # Executa a chamada inicial ao backend
    load_habits()

# if __name__ == "__main__":
#     ft.run(main)
if __name__ == "__main__":
    # Ignoramos o flet run e rodamos direto no python para evitar travamentos do CLI
    ft.app(main, view=ft.AppView.WEB_BROWSER, port=8550)
