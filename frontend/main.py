import flet as ft
from habit_service import HabitService

service = HabitService()


def main(page: ft.Page):
    page.title = "HabitFlow"
    page.theme_mode = ft.ThemeMode.DARK
    page.padding = 24
    page.window.width = 900

    habits: list[dict] = []

    # ── Snackbar ──────────────────────────────────────────────────────────────

    def show_error(msg: str):
        page.snack_bar = ft.SnackBar(
            ft.Text(msg, color=ft.Colors.WHITE),
            bgcolor=ft.Colors.RED_700,
            open=True,
        )
        page.update()

    # ── Tabela ────────────────────────────────────────────────────────────────

    table = ft.DataTable(
        expand=True,
        columns=[
            ft.DataColumn(ft.Text("Nome", weight=ft.FontWeight.BOLD)),
            ft.DataColumn(ft.Text("Frequência", weight=ft.FontWeight.BOLD)),
            ft.DataColumn(ft.Text("Criado em", weight=ft.FontWeight.BOLD)),
            ft.DataColumn(ft.Text("Ações", weight=ft.FontWeight.BOLD)),
        ],
        rows=[],
    )

    def load_habits():
        try:
            data = service.get_all()
            habits.clear()
            habits.extend(data)
            table.rows = [_build_row(h) for h in habits]
            page.update()
        except Exception as e:
            show_error(f"Erro ao carregar hábitos: {e}")

    def _build_row(habit: dict) -> ft.DataRow:
        return ft.DataRow(cells=[
            ft.DataCell(ft.Text(habit.get("name", ""))),
            ft.DataCell(ft.Text(habit.get("frequency", ""))),
            ft.DataCell(ft.Text(habit.get("createdAt", "") or "")),
            ft.DataCell(ft.Row(spacing=0, controls=[
                ft.IconButton(
                    ft.Icons.EDIT_OUTLINED,
                    tooltip="Editar",
                    on_click=lambda e, h=habit: _open_edit(h),
                ),
                ft.IconButton(
                    ft.Icons.DELETE_OUTLINE,
                    tooltip="Deletar",
                    icon_color=ft.Colors.RED_400,
                    on_click=lambda e, h=habit: _open_delete(h),
                ),
            ])),
        ])

    # ── Dialog criar / editar ─────────────────────────────────────────────────

    _edit_id: dict = {"value": None}

    field_name = ft.TextField(label="Nome", autofocus=True)
    field_desc = ft.TextField(label="Descrição")
    field_freq = ft.Dropdown(
        label="Frequência",
        value="DAILY",
        options=[
            ft.dropdown.Option("DAILY", "Diário"),
            ft.dropdown.Option("WEEKLY", "Semanal"),
            ft.dropdown.Option("MONTHLY", "Mensal"),
        ],
    )
    form_title = ft.Text("", size=18, weight=ft.FontWeight.BOLD)

    def _save(e):
        name = (field_name.value or "").strip()
        if not name:
            field_name.error_text = "Campo obrigatório"
            page.update()
            return
        field_name.error_text = None
        try:
            if _edit_id["value"] is None:
                service.create(name, field_desc.value or "", field_freq.value)
            else:
                service.update(_edit_id["value"], name, field_desc.value or "", field_freq.value)
            form_dialog.open = False
            page.update()
            load_habits()
        except Exception as ex:
            show_error(str(ex))

    def _close_form(e):
        form_dialog.open = False
        page.update()

    form_dialog = ft.AlertDialog(
        modal=True,
        title=form_title,
        content=ft.Column(
            tight=True,
            width=320,
            controls=[field_name, field_desc, field_freq],
        ),
        actions=[
            ft.TextButton("Cancelar", on_click=_close_form),
            ft.FilledButton("Salvar", on_click=_save),
        ],
        actions_alignment=ft.MainAxisAlignment.END,
    )
    page.overlay.append(form_dialog)

    def _open_create(e):
        _edit_id["value"] = None
        form_title.value = "Novo Hábito"
        field_name.value = ""
        field_desc.value = ""
        field_freq.value = "DAILY"
        field_name.error_text = None
        form_dialog.open = True
        page.update()

    def _open_edit(habit: dict):
        _edit_id["value"] = habit["id"]
        form_title.value = "Editar Hábito"
        field_name.value = habit.get("name", "")
        field_desc.value = habit.get("description", "") or ""
        field_freq.value = habit.get("frequency", "DAILY")
        field_name.error_text = None
        form_dialog.open = True
        page.update()

    # ── Dialog deletar ────────────────────────────────────────────────────────

    _delete_id: dict = {"value": None}
    delete_label = ft.Text()

    def _confirm_delete(e):
        try:
            service.delete(_delete_id["value"])
            delete_dialog.open = False
            page.update()
            load_habits()
        except Exception as ex:
            show_error(str(ex))

    def _close_delete(e):
        delete_dialog.open = False
        page.update()

    delete_dialog = ft.AlertDialog(
        modal=True,
        title=ft.Text("Confirmar exclusão"),
        content=delete_label,
        actions=[
            ft.TextButton("Cancelar", on_click=_close_delete),
            ft.FilledButton(
                "Deletar",
                on_click=_confirm_delete,
                style=ft.ButtonStyle(color=ft.Colors.RED),
            ),
        ],
        actions_alignment=ft.MainAxisAlignment.END,
    )
    page.overlay.append(delete_dialog)

    def _open_delete(habit: dict):
        _delete_id["value"] = habit["id"]
        delete_label.value = f"Tem certeza que deseja deletar \"{habit['name']}\"?"
        delete_dialog.open = True
        page.update()

    # ── Layout ────────────────────────────────────────────────────────────────

    page.add(
        ft.Row(
            controls=[
                ft.Text("HabitFlow", size=28, weight=ft.FontWeight.BOLD),
                ft.FilledButton("Novo Hábito", icon=ft.Icons.ADD, on_click=_open_create),
            ],
            alignment=ft.MainAxisAlignment.SPACE_BETWEEN,
        ),
        ft.Divider(height=16),
        ft.Row(controls=[table], scroll=ft.ScrollMode.AUTO),
    )

    load_habits()


ft.run(main, view=ft.AppView.WEB_BROWSER, port=8550)
