import SwiftUI

struct ContentView: View {
    var body: some View {
        NavigationStack {
            VStack(alignment: .leading, spacing: 18) {
                Image("AppLogo")
                    .resizable()
                    .scaledToFit()
                    .frame(width: 84, height: 84)
                    .accessibilityHidden(true)

                VStack(alignment: .leading, spacing: 8) {
                    Text("Quri")
                        .font(.largeTitle.weight(.bold))
                    Text("Planifica tus metas, reparte ingresos y revisa tu progreso financiero desde iPhone.")
                        .font(.body)
                        .foregroundStyle(.secondary)
                }

                VStack(spacing: 12) {
                    FeatureRow(title: "Reparto automatico", detail: "Motor compartido con Android mediante Kotlin Multiplatform.")
                    FeatureRow(title: "Recompensas", detail: "Reglas de puntos reutilizadas desde el modulo shared.")
                    FeatureRow(title: "Beta iOS", detail: SharedStatus.text)
                }

                Spacer()
            }
            .padding(24)
            .navigationTitle("Quri")
        }
    }
}

private struct FeatureRow: View {
    let title: String
    let detail: String

    var body: some View {
        VStack(alignment: .leading, spacing: 4) {
            Text(title)
                .font(.headline)
            Text(detail)
                .font(.subheadline)
                .foregroundStyle(.secondary)
        }
        .frame(maxWidth: .infinity, alignment: .leading)
        .padding(14)
        .background(Color(.secondarySystemBackground), in: RoundedRectangle(cornerRadius: 10))
    }
}

#Preview {
    ContentView()
}