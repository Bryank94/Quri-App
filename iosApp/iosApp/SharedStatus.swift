#if canImport(shared)
import shared
#endif

import Foundation

enum SharedStatus {
    static var text: String {
        #if canImport(shared)
        return "Framework KMP enlazado. Lista para compilar desde Xcode."
        #else
        return "Abre el proyecto en Xcode para generar el framework KMP."
        #endif
    }
}