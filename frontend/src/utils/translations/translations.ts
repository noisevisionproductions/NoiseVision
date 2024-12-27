export const translations = {
    pl: {
        common: {
            cancel: "Anuluj",
            remove: "Usuń",
            back: "Cofnij",
            errorWhileLoading: "Błąd podczas ładowania",
            backToHome: "Wróć do strony głównej",
            loading: "Ładowanie...",
            redirecting: "Przekierowywanie...",
            continue: "Kontynuuj",
            other: "Inny",
            unknown: "Nieznany",
            invalidIp: "Nieprawidłowe IP",
        },
        errors: {
            noPermissions: "Brak uprawnień do wykonania tej akcji",
            unexpected: "Wystąpił nieoczekiwany błąd",
            sessionExpired: "Sesja wygasła. Zaloguj się ponownie",
            noPermissionsDescription: "Nie posiadasz uprawnień do wykonania tej akcji",
            contributor: {
                add: "Wystąpił błąd podczas dodawania kontrybutora"
            },
            feature: {
                update: "Wystąpił błąd podczas aktualizacji funkcji"
            },
            image: {
                add: "Błąd podczas dodawania obrazu",
                delete: "Błąd podczas usuwania obrazu",
                missingProjectId: "Brak ID projektu"
            }
        },
        header: {
            title: "Moje Portfolio",
            navigation: {
                addProject: "Dodaj projekt",
                projects: "Projekty",
                contact: "Kontakt",
                login: "Zaloguj się",
                logout: "Wyloguj się",
                kafkaDashboard: "Kafka"
            }
        },
        login: {
            email: "Wprowadź e-mail",
            password: "Wprowadź hasło",
            submit: "Zaloguj",
            register: "Kliknij, aby utworzyć nowe konto",
            title: "Logowanie",
            alreadyLoggedIn: "Już jesteś zalogowany",
            errors: {
                emailRequired: "Email jest wymagany",
                passwordRequired: "Hasło jest wymagane",
                invalidCredentials: "Nieprawidłowy email lub hasło",
                passwordLength: "Hasło musi mieć co najmniej 6 znaków",
                generic: "Wystąpił błąd podczas logowania"
            },
            success: {
                title: "Zalogowano pomyślnie",
                description: "Zostaniesz przekierowany do strony głównej"
            }
        },
        logout: {
            success: {
                title: "Wylogowano pomyślnie",
                description: "Zostałeś pomyślnie wylogowany"
            }
        },
        register: {
            email: "Wprowadź e-mail",
            name: "Imię (opcjonalnie)",
            companyName: "Nazwa firmy (opcjonalnie)",
            programmingLanguages: "Języki programowania (opcjonalnie)",
            password: "Wprowadź hasło",
            confirmPassword: "Potwierdź hasło",
            submit: "Utwórz konto",
            title: "Tworzenie nowego konta",
            success: {
                title: "Konto zostało utworzone",
                description: "Twoje konto zostało pomyślnie utworzone."
            },
            errors: {
                emailExists: "Ten email jest już zajęty",
                passwordLength: "Hasło musi mieć co najmniej 6 znaków",
                emailRequired: "Email jest wymagany",
                passwordRequired: "Hasło jest wymagane",
                invalidEmail: "Nieprawidłowy format email",
                generic: "Wystąpił błąd podczas rejestracji",
                registrationBlocked: "Nie możesz utworzyć nowego konta przez godzinę od ostatniej rejestracji.",
            }
        },
        heroSection: {
            title: "Software Developer",
            description: "Specjalizuję się w tworzeniu aplikacji Android oraz Web."
        },
        projectSection: {
            title: "Projekty",
            noProjects: "Nie znaleziono projektów"
        },
        contactSection: {
            contact: "Kontakt",
            message: "Wiadomość",
            submit: "Wyślij"
        },
        projectForm: {
            imageAlt: "Obraz",
            createTitle: "Dodaj nowy projekt",
            editTitle: "Edytuj projekt",
            projectName: "Nazwa projektu",
            description: "Opis projektu",
            repositoryUrl: "Link do repozytorium",
            status: "Status projektu",
            statuses: {
                inProgress: "W trakcie rozwoju",
                completed: "Zakończony",
                archived: "Zarchiwizowany"
            },
            startDate: "Data rozpoczęcia",
            endDate: "Data zakończenia",
            features: "Funkcje",
            addFeature: "Dodaj funkcjonalność",
            contributors: "Współtwórcy",
            contributor: "Współtwórca",
            contributorName: "Imię/Nickname",
            contributorRole: "Rola",
            contributorProfileUrl: "Profil",
            addContributor: "Dodaj współtwórcę",
            technologies: "Technologie",
            caption: "Opis",
            addImage: "Dodaj obrazy",
            saveProject: "Zapisz projekt",
            updateProject: "Zaktualizuj projekt",
            createCaption: "Dodaj opis do obrazu...",
            saveImage: "Zapisz obraz",
            fetchError: "Nie udało się pobrać danych projektu",
            successTitle: "Projekt został stworzony",
            successDescription: "Projekt utworzony, kliknij OK, aby przejść do menu głównego",
            projectForm: "Zostaniesz przeniesiony do Menu Głównego",
            imageLoadError: "Błąd podczas ładowania obrazu",
            imageLoadErrorDetail: "Nie udało się załadować obrazu. Plik mógł zostać usunięty."
        },
        projectDetails: {
            confirmDelete: "Czy na pewno chcesz usunąć ten projekt?",
            deleteError: "Nie udało się usunąć projektu",
            error: "Wystąpił nieoczekiwany błąd",
            backToMainMenu: "Wróć do strony głównej",
            back: "Wróć",
            edit: "Edytuj",
            delete: "Usuń",
            startDate: "Data rozpoczęcia",
            endDate: "Data zakończenia",
            viewRepository: "Repozytorium",
            features: "Funkcje",
            contributors: "Współtwórcy",
            viewProfile: "Zobacz profil",
            confirmImageDelete: "Czy na pewno chcesz usunąć ten obraz?",
            deleteImageError: "Błąd podczas usuwania obrazu"
        },
        kafka: {
            dashboard: {
                menu: {
                    overview: "Przegląd",
                    registrations: "Rejestracje"
                },
                registrations: {
                    title: 'Panel Statystyk Rejestracji',
                    timeAnalysis: "Analiza czasowa",
                    latestRegistrations: "Ostatnie rejestracje",
                    registrationsCount: "Liczba rejestracji",
                    chart: {
                        title: 'Statystyki Rejestracji (7 dni)'
                    },
                    recent: {
                        title: 'Ostatnie Rejestracje'
                    },
                    table: {
                        status: 'Status',
                        email: 'Email',
                        date: 'Data',
                        source: 'Źródło',
                        noData: 'Brak danych do wyświetlenia'
                    },
                    stats: {
                        total: 'Wszystkie Rejestracje',
                        successful: 'Udane Rejestracje',
                        failed: 'Nieudane Rejestracje',
                        successRate: 'Współczynnik Sukcesu'
                    },
                    registrationSources: {
                        registrationSource: "Źródło rejestracji",
                        registrationSources: "Źródła rejestracji",
                        browsers: "Przeglądarki użytkowników",
                        schedule: "Przegląd rejestracji w ciągu dnia",
                        geoDistribution: "Dystrybucja geograficzna"
                    }
                },
                overview: {
                    title: 'Przegląd systemu',
                    totalRegistrations: 'Wszystkie rejestracje',
                    totalRegistrationsDesc: 'Łączna liczba rejestracji w systemie',
                    registrationSuccess: 'Skuteczność rejestracji',
                    registrationSuccessDesc: 'Procent udanych rejestracji',
                    totalProjects: 'Liczba projektów',
                    totalProjectsDesc: 'Łączna liczba projektów w systemie',
                    systemMetrics: 'Dostępność systemu',
                    systemMetricsDesc: 'Średnia dostępność systemu',
                    recentActivity: 'Ostatnia aktywność',
                    noRecentActivity: 'Brak ostatniej aktywności',
                    quickActions: 'Szybkie akcje',
                    noQuickActions: 'Brak dostępnych akcji'
                },
            },
            errors: {
                statsFetchFailed: "Błąd podczas pobierania statystyk",
                recentRegistrationsFetchFailed: "Błąd podczas pobierania ostatnich zdarzeń",
                registrationsForPeriodFetchFailed: "Błąd podczas pobierania zdarzeń z okresu"
            }
        }
    },
    en: {
        common: {
            cancel: "Cancel",
            remove: "Delete",
            back: "Back",
            errorWhileLoading: "Error while loading",
            backToHome: "Back to main page",
            loading: "Loading...",
            redirecting: "Redirecting...",
            continue: "Continue",
            other: "Other",
            unknown: "Unknown",
            invalidIp: "Invalid IP",
        },
        errors: {
            noPermissions: "You don't have permission to perform this action",
            unexpected: "An unexpected error occurred",
            sessionExpired: "Session expired. Please log in again",
            noPermissionsDescription: "You don't have permission to perform this action",
            contributor: {
                add: "An error occurred while adding a contributor."
            },
            feature: {
                update: "An error occurred while updating a feature"
            },
            image: {
                add: "An error occurred while adding an image",
                delete: "An error occurred while deleting an image",
                missingProjectId: "Project ID is missing"
            }
        },
        header: {
            title: "My Portfolio",
            navigation: {
                addProject: "Add project",
                projects: "Projects",
                contact: "Contact",
                login: "Log in",
                logout: "Log out",
                kafkaDashboard: "Kafka"
            }

        },
        login: {
            email: "Enter e-mail",
            password: "Enter password",
            submit: "Login",
            register: "Click here to create a new account",
            title: "Log in",
            alreadyLoggedIn: "You are already logged in",
            errors: {
                emailRequired: "Email is required",
                passwordRequired: "Password is required",
                passwordLength: "Password must be at least 6 characters long",
                invalidCredentials: "Invalid email or password",
                generic: "An error occurred while logging in"
            },
            success: {
                title: "Logged in successfully",
                description: "You will be redirected to the home page"

            }
        },
        logout: {
            success: {
                title: "Successfully logged out",
                description: "You have been successfully logged out"
            }
        },
        register: {
            email: "Enter e-mail",
            name: "Name (optional)",
            companyName: "Company name (optional)",
            programmingLanguages: "Programming languages (optional)",
            password: "Enter password",
            confirmPassword: "Confirm password",
            submit: "Create account",
            title: "New account creating",
            success: {
                title: "Account created",
                description: "Your account has been successfully created."
            },
            errors: {
                emailExists: "This email is already taken",
                passwordLength: "Password must be at least 6 characters long",
                emailRequired: "Email is required",
                passwordRequired: "Password is required",
                invalidEmail: "Invalid email format",
                generic: "Registration failed",
                registrationBlocked: "You cannot create a new account for one hour after the last registration.",
            }
        },
        heroSection: {
            title: "Software Developer",
            description: "I specialize in creating Android and Web applications."
        },
        projectSection: {
            title: "Projects",
            noProjects: "No projects found"
        },
        contactSection: {
            contact: "Contact",
            message: "Message",
            submit: "Submit"
        },
        projectForm: {
            imageAlt: "Image",
            createTitle: "Add new project",
            editTitle: "Edit project",
            projectName: "Project name",
            description: "Project description",
            repositoryUrl: "Repository link",
            status: "Project status",
            statuses: {
                inProgress: "In progress",
                completed: "Completed",
                archived: "Archived"
            },
            startDate: "Start date",
            endDate: "End date",
            features: "Features",
            contributors: "Contributors",
            contributor: "Contributor",
            contributorName: "Name/Nickname",
            contributorRole: "Role",
            contributorProfileUrl: "Profile",
            addContributor: "Add contributor",
            addFeature: "Add a feature",
            technologies: "Technologies",
            caption: "Caption",
            addImage: "Add images",
            saveProject: "Save project",
            updateProject: "Update project",
            createCaption: "Add caption for image...",
            saveImage: "Save image",
            fetchError: "Failed to get project data",
            successTitle: "Project has been created",
            successDescription: "Project created, click OK to switch to Main Menu",
            projectForm: "You will be taken to the Main Menu",
            imageLoadError: "Failed to load image",
            imageLoadErrorDetail: "Unable to load image. The file may have been moved or deleted."
        },
        projectDetails: {
            confirmDelete: "Are you sure you want to delete this project?",
            deleteError: "Failed to delete project",
            error: "An unexpected error occurred",
            backToMainMenu: "Back to main page",
            back: "Back",
            edit: "Edit",
            delete: "Delete",
            startDate: "Start date",
            endDate: "End date",
            viewRepository: "Repository",
            features: "Features",
            contributors: "Contributors",
            viewProfile: "View profile",
            confirmImageDelete: "Are you sure you want to delete this image?",
            deleteImageError: "Error occurred while deleting image"
        },
        kafka: {
            dashboard: {
                menu: {
                    overview: "Overview",
                    registrations: "Registrations"
                },
                registrations: {
                    title: 'Registration Statistics Dashboard',
                    timeAnalysis: "Time analysis",
                    latestRegistrations: "Latest registrations",
                    registrationsCount: "Number of registrations",
                    chart: {
                        title: 'Registration Statistics (7 days)'
                    },
                    recent: {
                        title: 'Recent Registrations'
                    },
                    table: {
                        status: 'Status',
                        email: 'Email',
                        date: 'Date',
                        source: 'Source',
                        noData: 'No data to display'
                    },
                    stats: {
                        total: 'Total Registrations',
                        successful: 'Successful Registrations',
                        failed: 'Failed Registrations',
                        successRate: 'Success Rate'
                    },
                    registrationSources: {
                        registrationSources: "Registration sources",
                        registrationSource: "Registration source",
                        browsers: "User's browsers",
                        schedule: "Registration schedule for the day",
                        geoDistribution: "Geographic distribution"
                    }
                },
                overview: {
                    title: 'System Overview',
                    totalRegistrations: 'Total Registrations',
                    totalRegistrationsDesc: 'Total number of registrations in system',
                    registrationSuccess: 'Registration Success',
                    registrationSuccessDesc: 'Percentage of successful registrations',
                    totalProjects: 'Total Projects',
                    totalProjectsDesc: 'Total number of projects in system',
                    systemMetrics: 'System Availability',
                    systemMetricsDesc: 'Average system availability',
                    recentActivity: 'Recent Activity',
                    noRecentActivity: 'No recent activity',
                    quickActions: 'Quick Actions',
                    noQuickActions: 'No actions available'
                },
            },
            errors: {
                statsFetchFailed: "Error fetching stats",
                recentRegistrationsFetchFailed: "Error fetching recent events",
                registrationsForPeriodFetchFailed: "Error fetching period events"
            }
        },
    }
};

export type Language = 'pl' | 'en'