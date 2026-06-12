import flet as ft

def get_frequency_label(freq):
    mapping = {"DAILY": "DIÁRIO", "WEEKLY": "SEMANAL", "MONTHLY": "MENSAL"}
    return mapping.get(freq, "HÁBITO")

def create_habit_card(h_id, h_name, h_freq_raw, on_view, on_edit, on_delete):
    h_freq = get_frequency_label(h_freq_raw)
    
    return ft.Container(
        bgcolor="#0B132B",
        border_radius=15,
        padding=15,
        content=ft.Row([
            ft.Checkbox(fill_color="#1A2238", check_color="#FF6B6B"),
            ft.Icon(ft.Icons.PUSH_PIN, color="#FF6B6B", size=20),
            ft.Column([
                ft.Text(h_name, weight=ft.FontWeight.BOLD, color=ft.Colors.WHITE),
                ft.Container(
                    bgcolor="#3A1E2B",
                    padding=ft.Padding.symmetric(horizontal=6, vertical=2),
                    border_radius=5,
                    content=ft.Text(h_freq, size=10, color="#FF6B6B", weight=ft.FontWeight.BOLD)
                )
            ], expand=True, spacing=2),
            ft.Row([
                ft.IconButton(ft.Icons.INSERT_DRIVE_FILE, icon_color="#5C6B89", icon_size=18, on_click=lambda e: on_view(h_id)),
                ft.IconButton(ft.Icons.EDIT, icon_color="#5C6B89", icon_size=18, on_click=lambda e: on_edit(h_id)),
                ft.IconButton(ft.Icons.DELETE, icon_color="#5C6B89", icon_size=18, on_click=lambda e: on_delete(h_id))
            ], spacing=0)
        ], alignment=ft.MainAxisAlignment.SPACE_BETWEEN)
    )
