import flet as ft


class Modals:
    def __init__(self, page: ft.Page, on_submit):
        self.page = page
        self.on_submit = on_submit
        self.current_editing_id = None

        # Form fields
        self.name_field = ft.TextField(
            label="Nome do Hábito",
            bgcolor="#1A2238",
            border_color="transparent",
            filled=True,
        )
        self.desc_field = ft.TextField(
            label="Descrição",
            bgcolor="#1A2238",
            border_color="transparent",
            filled=True,
            multiline=True,
        )
        self.freq_dropdown = ft.Dropdown(
            label="Frequência",
            bgcolor="#1A2238",
            border_color="transparent",
            filled=True,
            options=[
                ft.DropdownOption("DAILY", "Diário"),
                ft.DropdownOption("WEEKLY", "Semanal"),
                ft.DropdownOption("MONTHLY", "Mensal"),
            ],
            value="DAILY",
        )

        self.modal_title = ft.Text("Novo Hábito")
        modal_content = ft.Column(
            [self.name_field, self.desc_field, self.freq_dropdown], tight=True
        )
        self.form_modal = ft.AlertDialog(
            modal=True,
            title=self.modal_title,
            content=modal_content,
            bgcolor="#0B132B",
            actions=[
                ft.TextButton("Cancelar", on_click=self.close_form_modal),
                ft.FilledButton(
                    "Salvar",
                    style=ft.ButtonStyle(bgcolor="#FF6B6B", color=ft.Colors.WHITE),
                    on_click=self.handle_submit,
                ),
            ],
            actions_alignment=ft.MainAxisAlignment.END,
        )

        # View details modal
        self.view_details = ft.Text("", selectable=True)
        self.view_modal = ft.AlertDialog(
            title=ft.Text("Detalhes do Hábito"),
            content=self.view_details,
            bgcolor="#1A2238",
            actions=[ft.TextButton("Fechar", on_click=self.close_view_modal)],
        )

        page.overlay.extend([self.form_modal, self.view_modal])

    def close_form_modal(self, e):
        self.form_modal.open = False
        self.page.update()

    def close_view_modal(self, e):
        self.view_modal.open = False
        self.page.update()

    def handle_submit(self, e):
        data = {
            "name": self.name_field.value,
            "description": self.desc_field.value,
            "frequency": self.freq_dropdown.value,
        }
        self.on_submit(self.current_editing_id, data)

    def open_create(self):
        self.current_editing_id = None
        self.modal_title.value = "Novo Hábito"
        self.name_field.value = ""
        self.desc_field.value = ""
        self.freq_dropdown.value = "DAILY"
        self.form_modal.open = True
        self.page.update()

    def open_edit(self, habit_id, name, desc, freq):
        self.current_editing_id = habit_id
        self.modal_title.value = "Editar Hábito"
        self.name_field.value = name
        self.desc_field.value = desc
        self.freq_dropdown.value = freq
        self.form_modal.open = True
        self.page.update()

    def open_view(self, data):
        self.view_details.value = (
            f"ID: {data.get('id')}\n"
            f"Nome: {data.get('name')}\n"
            f"Descrição: {data.get('description', 'Sem descrição')}\n"
            f"Frequência: {data.get('frequency')}\n"
        )
        self.view_modal.open = True
        self.page.update()
