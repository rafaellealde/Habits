import os
import flet as ft
from services.api_service import get_habits, get_habit, create_habit, update_habit, delete_habit
from components.header_banner import HeaderBanner
from components.habit_card import create_habit_card
from components.modals import Modals


def main(page: ft.Page):
    page.title = "HabitFlow"
    page.theme_mode = ft.ThemeMode.DARK
    page.bgcolor = "#0B132B"
    page.padding = 0
    page.horizontal_alignment = ft.CrossAxisAlignment.CENTER

    habits_data = []

    # -------------------------------------------------------
    # RESPONSIVE LAYOUT HELPER
    # -------------------------------------------------------
    def content_width():
        if page.width and page.width < 600:
            return page.width
        return 500

    # -------------------------------------------------------
    # SNACKBAR HELPER (single update via load_habits)
    # -------------------------------------------------------
    pending_message = {"text": None, "is_error": False}

    def queue_message(message, is_error=False):
        pending_message["text"] = message
        pending_message["is_error"] = is_error

    def flush_message():
        if pending_message["text"]:
            color = ft.Colors.ERROR if pending_message["is_error"] else ft.Colors.GREEN
            page.snack_bar = ft.SnackBar(
                ft.Text(pending_message["text"]), bgcolor=color
            )
            page.snack_bar.open = True
            pending_message["text"] = None

    def show_message(message, is_error=False):
        color = ft.Colors.ERROR if is_error else ft.Colors.GREEN
        page.snack_bar = ft.SnackBar(ft.Text(message), bgcolor=color)
        page.snack_bar.open = True
        page.update()

    # -------------------------------------------------------
    # MODAL SUBMIT CALLBACK
    # -------------------------------------------------------
    def handle_submit(habit_id, data):
        if habit_id is None:
            success, err = create_habit(data)
            msg = "Hábito criado com sucesso!"
        else:
            success, err = update_habit(habit_id, data)
            msg = "Hábito atualizado!"

        if success:
            # Close modal without triggering an extra update
            modals.form_modal.open = False
            queue_message(msg)
            load_habits()  # single page.update() at the end
        else:
            show_message(err or "Erro ao salvar.", is_error=True)

    modals = Modals(page, handle_submit)

    # -------------------------------------------------------
    # ACTION CALLBACKS
    # -------------------------------------------------------
    def on_view(h_id):
        data, err = get_habit(h_id)
        if data:
            modals.open_view(data)
        else:
            show_message(err, is_error=True)

    def on_edit(h_id):
        h = next((x for x in habits_data if x.get("id") == h_id), None)
        if h:
            modals.open_edit(
                h_id,
                h.get("name", ""),
                h.get("description", ""),
                h.get("frequency", "DAILY"),
            )

    def on_delete(h_id):
        success, err = delete_habit(h_id)
        if success:
            queue_message("Hábito deletado!")
            load_habits()
        else:
            show_message(err, is_error=True)

    # -------------------------------------------------------
    # HABITS LIST
    # -------------------------------------------------------
    habits_list = ft.Column(spacing=10, expand=True, scroll=ft.ScrollMode.AUTO)

    def load_habits():
        nonlocal habits_data
        habits_list.controls.clear()

        data, err = get_habits()
        if data is not None:
            habits_data = data
            total = len(habits_data)
            header.set_progress(min(total / 10, 1.0))  # progress up to 10 habits

            if not habits_data:
                habits_list.controls.append(
                    ft.Text(
                        "Nenhum hábito ainda. Toque em + para adicionar!",
                        color=ft.Colors.WHITE_54,
                        text_align=ft.TextAlign.CENTER,
                    )
                )
            for h in habits_data:
                card = create_habit_card(
                    h.get("id"),
                    h.get("name", "Sem Nome"),
                    h.get("frequency", "DAILY"),
                    on_view,
                    on_edit,
                    on_delete,
                )
                habits_list.controls.append(card)
        else:
            habits_list.controls.append(
                ft.Text(
                    f"Backend indisponível: {err}",
                    color=ft.Colors.ERROR,
                    text_align=ft.TextAlign.CENTER,
                )
            )

        flush_message()
        page.update()

    # -------------------------------------------------------
    # BUILD LAYOUT
    # -------------------------------------------------------
    header = HeaderBanner(page=page, on_date_change=lambda d: load_habits())

    main_section = ft.Container(
        bgcolor="#1A2238",
        border_radius=ft.BorderRadius.only(top_left=30, top_right=30),
        padding=30,
        expand=True,
        content=ft.Column([
            ft.Row([
                ft.Icon(ft.Icons.WB_SUNNY, color="#FFD166"),
                ft.Text(
                    "HÁBITOS",
                    size=16,
                    weight=ft.FontWeight.BOLD,
                    color="#FFD166",
                ),
            ]),
            ft.Container(height=10),
            habits_list,
        ]),
    )

    page.floating_action_button = ft.FloatingActionButton(
        content=ft.Icon(ft.Icons.ADD, color=ft.Colors.WHITE),
        bgcolor="#FF6B6B",
        on_click=lambda e: modals.open_create(),
    )

    layout = ft.Container(
        width=content_width(),
        expand=True,
        content=ft.Column(
            [header.control, main_section],
            spacing=0,
            expand=True,
        ),
    )

    # -------------------------------------------------------
    # RESPONSIVE RESIZE HANDLER
    # -------------------------------------------------------
    def on_resize(e):
        layout.width = content_width()
        page.update()

    page.on_resized = on_resize

    page.add(layout)
    load_habits()


if __name__ == "__main__":
    # Lê a porta que a nuvem do Render exige, ou usa 8000 por padrão localmente
    port_env = int(os.getenv("PORT", 8000))
    ft.app(target=main, view=ft.AppView.WEB_BROWSER, port=port_env)