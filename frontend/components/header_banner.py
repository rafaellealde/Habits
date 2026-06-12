import flet as ft
from datetime import datetime, timedelta


class HeaderBanner:
    def __init__(self, page: ft.Page, on_date_change=None):
        self.page = page
        self.current_date = datetime.now()
        self.on_date_change = on_date_change

        self.time_text = ft.Text(
            self.current_date.strftime("%H:%M"),
            size=36,
            weight=ft.FontWeight.BOLD,
            color=ft.Colors.WHITE,
        )
        self.date_text = ft.Text(
            self._format_date(),
            color=ft.Colors.WHITE,
            weight=ft.FontWeight.BOLD,
        )
        self.progress_bar = ft.ProgressBar(
            value=0.0, color="#FFD166", bgcolor=ft.Colors.WHITE_30
        )
        self.progress_label = ft.Text(
            "PROGRESSO 0%",
            size=10,
            weight=ft.FontWeight.BOLD,
            color=ft.Colors.WHITE,
        )

        # DatePicker registrado no overlay da page
        self.date_picker = ft.DatePicker(
            first_date=datetime(2020, 1, 1),
            last_date=datetime(2030, 12, 31),
            on_change=self._on_date_picked,
        )
        page.overlay.append(self.date_picker)

        self.control = self._build()

    def _format_date(self):
        return self.current_date.strftime("%d/%m/%Y")

    def set_progress(self, value: float):
        self.progress_bar.value = value
        self.progress_label.value = f"PROGRESSO {int(value * 100)}%"

    def _on_date_picked(self, e):
        if e.control.value:
            self.current_date = e.control.value
            self.date_text.value = self._format_date()
            if self.on_date_change:
                self.on_date_change(self.current_date)
            self.page.update()

    def _open_calendar(self, e):
        self.date_picker.value = self.current_date
        self.date_picker.open = True
        self.page.update()

    def _prev_date(self, e):
        self.current_date -= timedelta(days=1)
        self.date_text.value = self._format_date()
        if self.on_date_change:
            self.on_date_change(self.current_date)
        self.page.update()

    def _next_date(self, e):
        self.current_date += timedelta(days=1)
        self.date_text.value = self._format_date()
        if self.on_date_change:
            self.on_date_change(self.current_date)
        self.page.update()

    def _build(self):
        banner = ft.Container(
            gradient=ft.LinearGradient(
                begin=ft.Alignment(-1.0, -1.0),
                end=ft.Alignment(1.0, 1.0),
                colors=["#FF6B6B", "#FF8E53"],
            ),
            border_radius=ft.BorderRadius.only(bottom_left=30, bottom_right=30),
            padding=ft.Padding.all(30),
            content=ft.Column([
                ft.Row([
                    ft.Column([
                        ft.Text(
                            "Olá, Bem vindo (a)!",
                            size=22,
                            weight=ft.FontWeight.BOLD,
                            color=ft.Colors.WHITE,
                        ),
                        ft.Text(
                            "HOJE",
                            size=12,
                            weight=ft.FontWeight.W_500,
                            color=ft.Colors.WHITE_70,
                        ),
                    ]),
                    self.time_text,
                ], alignment=ft.MainAxisAlignment.SPACE_BETWEEN),
                ft.Container(height=20),
                self.progress_label,
                self.progress_bar,
            ]),
        )

        date_selector = ft.Container(
            padding=ft.Padding.symmetric(horizontal=20, vertical=15),
            content=ft.Container(
                bgcolor="#1A2238",
                border_radius=20,
                padding=15,
                content=ft.Row([
                    ft.IconButton(
                        ft.Icons.CHEVRON_LEFT,
                        icon_color=ft.Colors.WHITE_54,
                        on_click=self._prev_date,
                    ),
                    ft.GestureDetector(
                        on_tap=self._open_calendar,
                        content=ft.Row([
                            ft.Icon(ft.Icons.CALENDAR_TODAY, color="#FF6B6B", size=16),
                            self.date_text,
                        ]),
                    ),
                    ft.IconButton(
                        ft.Icons.CHEVRON_RIGHT,
                        icon_color=ft.Colors.WHITE_54,
                        on_click=self._next_date,
                    ),
                ], alignment=ft.MainAxisAlignment.SPACE_BETWEEN),
            ),
        )

        return ft.Column([banner, date_selector], spacing=0)
